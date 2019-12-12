#Run
ui-repl:
	cd ui && DEBUG=true clj  -A:dev:nrepl

#Build
ui-build:
		cd ui/ &&	lein with-profile prod compile
