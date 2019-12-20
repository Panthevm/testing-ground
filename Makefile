#Run
repl:
	cd ui && clj -A:dev:nrepl

#Build
ui-build:
	cd ui && clj -A:prod
