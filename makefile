DIR := ${CURDIR}

build:
	gradle build
	unzip $(CURDIR)/app/build/distributions/app.zip -d runs                                                                                                                  
  
unzip: 
	rm -rf runs
	unzip $(CURDIR)/app/build/distributions/app.zip
  
run:
	./runs/app/bin/app $(args)
  
clean:
	rm -rf runs
	rm -rf build

PC:
	./runs/app/bin/app -ip $(args) -wp $(args) -pc

CPC:
	./runs/app/bin/app -ip $(args) -wp $(args) -cpc
