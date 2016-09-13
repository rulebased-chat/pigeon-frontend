(ns ^:figwheel-no-load pigeon-frontend.dev
  (:require [pigeon-frontend.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3001/figwheel-ws"
  :jsload-callback core/mount-root)

(core/initialize-app! (.-token (.-localStorage js/window)))
