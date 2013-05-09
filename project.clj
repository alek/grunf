(defproject grunf "0.1.0-SNAPSHOT"
  :description "simple infrastructure monitoring toolkit"
  :url "http://bithacks.net/grunf"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
  	[org.clojure/clojure "1.5.1"]
  	[clj-http "0.4.1"]
	  [clojurewerkz/quartzite "1.0.1"]
  ]
  :main grunf.bin
)
