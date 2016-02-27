# ms-httpkit

This is a small addon for mount-ms derived from the tesla-microservice.  
Usage:


```clj
(def first-example-route
  (c/GET "/example1" []
    {:status 200
     :body   "<html><body><h1>MOUNT-MS example 1</h1></body></html>"}))

(def second-example-route
  (c/GET "/example2" []
    {:status 200
     :body   "<html><body><h1>MOUNT-MS example 2</h1></body></html>"}))

(defstate server
          :start (httpk/start-server
                   first-example-route
                   second-example-route)
          :stop (httpk/stop-server server))
```


kaibra