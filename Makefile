#Run
ui-repl:
	cd ui && DEBUG=true clj  -A:dev:nrepl

#Build
ui-build:
		cd ui && clj -m build
