# grunf

simple clojure-based infrastructure monitoring toolkit

## Usage

lein run -m grunf.bin (host list) (poll interval [sec])

Example:

lein run -m grunf.bin '["http://www.google.com" "http://facebook.com"] 10'

## License

Distributed under the Eclipse Public License, the same as Clojure.
