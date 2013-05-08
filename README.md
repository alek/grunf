# grunf

simple clojure-based infrastructure monitoring toolkit

## Usage

lein run -m grunf.bin (host list) (poll interval [sec])

Example:

lein run -m grunf.bin '(["http://www.google.com" "http://www.yahoo.com" "http://www.bing.com"] 1000)'

## License

Distributed under the Eclipse Public License, the same as Clojure.
