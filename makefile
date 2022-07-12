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
	./runs/app/bin/app -ip $(args1) -wp $(args2) -pc

cpc:
	./runs/app/bin/app -ip $(args1) -wp $(args2) -cpc

pipe:
	make pc
	make cpc

pyszz:
	python3 $(CURDIR)/app/src/main/java/hotdog/CPCMiner/pyszz/main.py $(CURDIR)/data/PC_list.json $(CURDIR)/app/src/main/java/hotdog/CPCMiner/pyszz/conf/agszz.yml $(args)

