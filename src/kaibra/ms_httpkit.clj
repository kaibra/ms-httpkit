(ns kaibra.ms-httpkit
  (:require [org.httpkit.server :as httpkit]
            [mount.core :refer [defstate]]
            [kaibra.stateful.configuring :as conf]
            [kaibra.stateful.app-status :as apps]
            [kaibra.stateful.health :as hlth]
            [clojure.tools.logging :as log]
            [ring.middleware.params :as middleware]
            [compojure.core :as c]))

(def default-port 3000)

(defn parser-string-config [element default-value]
  (or (conf/conf-prop element) default-value))

(defn parser-integer-config [element default-value]
  (try
    (Integer. (parser-string-config element default-value))
    (catch NumberFormatException e default-value)))

(defn server-config []
  {:port       (parser-integer-config :server-port default-port)
   :ip         (parser-string-config :server-bind "0.0.0.0")
   :thread     (parser-integer-config :server-thread 4)
   :queue-size (parser-integer-config :server-queue-size 20000)
   :max-body   (parser-integer-config :server-max-body 8388608)
   :max-line   (parser-integer-config :server-max-line 4096)})

(defn start-server [& handlers]
  (log/info "-> starting http-kit-server")
  (log/info "-> starting httpkit")
  (let [server-config (server-config)
        all-handlers (-> (apply c/routes
                                (apps/app-status-handler)
                                (hlth/health-handler)
                                handlers)
                         (middleware/wrap-params))]
    (log/info "Starting httpkit with port " (server-config :port) " and bind " (server-config :ip) ".")
    (httpkit/run-server all-handlers server-config)))

(defn stop-server [server]
  (log/info "-> stopping http-kit-server")
  (let [timeout (parser-integer-config :httpkit-timeout 100)]
    (if server
      (do
        (log/info "<- stopping httpkit with timeout:" timeout "ms")
        (server :timeout timeout))
      (log/info "<- stopping httpkit"))))
