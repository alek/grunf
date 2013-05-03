
; Simple Scheduled Monitoring Fetch Loop

(ns grunf.bin
	"Main function"
	(:require 	[clj-http.client :as client]	
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
		(println (fetchTime (get m "url")))))

(defn -main
	"Start Grunf. Pass remote hostname or config as first arg"
	[& argv]
	(def url (atom (first argv)))
	(try
		(println "initializing quartzite scheduler")
		(qs/initialize)
		(println "starting quartzite scheduler")
		(qs/start)
		(println "scheduler started")
		(let [job (j/build
					(j/of-type FetchJob)
					(j/using-job-data {"url" (first argv)})
					(j/with-identity (j/key "jobs.fetch.1")))
			  trigger (t/build
			  			(t/with-identity (t/key "triggers.1"))
			  			(t/start-now)
			  			(t/with-schedule (schedule
			  								(with-repeat-count 100)
			  								(with-interval-in-milliseconds 200))))]
			  (qs/schedule job trigger))
		(catch Exception e
			(error e "Error starting the app")
			)
	)	
)