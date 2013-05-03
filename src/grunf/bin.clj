
; Simple Scheduled Monitoring Fetch Loop

(ns grunf.bin
	"Main function"
	(:require [clj-http.client :as client]	
				[clojurewerkz.quartzite.scheduler :as qs]
				[clojurewerkz.quartzite.triggers :as t]
				[clojurewerkz.quartzite.jobs :as j]
				[clojurewerkz.quartzite.conversion :as qc]
		)
	(:use [clojurewerkz.quartzite.jobs :only [defjob]]
		[clojurewerkz.quartzite.schedule.simple :only [schedule with-repeat-count with-interval-in-milliseconds]])
	(:gen-class))

(def urls (atom #{})) ; set of active urls

(defn add
	"add endpoint url"
	[url]
	(swap! urls conj url)
	(str "foo, " url))

(defn fetch
	"fetch given url"
	[url]
	(client/get url))

(defn fetchTime
	"get url fetch time"
	[url]
	(map #(Double/parseDouble %) (re-seq #"[0-9]+.[0-9]+" (with-out-str (time (fetch url))))))

(defn error
	[exception message]
	(println message))

(defjob FetchJob
	[ctx]
	(let [m (qc/from-job-data ctx)]
		(println (conj (fetchTime (get m "url")) (get m "url")))))

(defn submitFetchJob
	"submit fetch job for execution"
	[url]
	(println (str "submitting url : " url))
	(let [job (j/build
				(j/of-type FetchJob)
				(j/using-job-data {"url" url})
				(j/with-identity (j/key (str "jobs.fetch." url) )))
		  trigger (t/build
			  		(t/with-identity (t/key (str "triggers." url) ))
			  		(t/start-now)
			  		(t/with-schedule (schedule
			  							(with-repeat-count 1000)
			  							(with-interval-in-milliseconds 500))))]
		  (qs/schedule job trigger)))

(defn -main
	"Start Grunf. Pass remote hostname or config as first arg"
	[& argv]
	(try
		(println "initializing quartzite scheduler")
		(qs/initialize)
		(println "starting quartzite scheduler")
		(qs/start)
		(println "scheduler started")
		(doseq [url argv] (submitFetchJob url))
		(catch Exception e
			(error e "Error starting the app")
			)
	)	
)