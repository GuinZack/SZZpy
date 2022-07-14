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

pc:
	./runs/app/bin/app -ip $(ip) -wp $(wp) -pc

pyszz:
	python3 $(CURDIR)/app/src/main/java/hotdog/CPCMiner/pyszz/main.py $(proj)_pc.json $(CURDIR)/app/src/main/java/hotdog/CPCMiner/pyszz/conf/rszz.yml $(wp)

cpc:
	./runs/app/bin/app -ip $(ip)_cpc.json -cpc

pipe:
	make pc ip=$(url) wp=$(wp)
	make pyszz proj=$(proj) wp=$(wp)
	make cpc ip=$(proj)

