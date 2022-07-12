# CPMiner
Chnage Prone Mining Tool built based on the approach introduced in Evaluating SZZ Implementations Through a Developer-informed Oracle (https://github.com/grosa1/pyszz)

---
## version control
pyszz

---
## Installation

To run PySZZ you need:

### Prerequisites

* You have to install [srcML](http://www.srcml.org/)

All external tools have to be available in your system's path.

### From the sources

Run the following command to install the required python dependencies:

```
pip3 install --no-cache-dir -r app/src/main/java/hotdog/CPCMiner/pyszz/requirements.txt
```
---
## Usage

To run the tool, simply execute the following command:
```
make clean
make build
make run args="<flag1> <argument1> <flag2> <argument2> ..."
```

#### Java spawning pyszz takes a lot slower.

Instead, we made shell commands to run the program partially in sequential order.

```
make pipe url="<GitHub URL>" wp="<clone directory>" proj="<Github project name>"
```

This command runs `make pc` command, `make pyszz` and `make cpc`

Please, go over makefile for detail.

### Options

`-ip` input path: it could be URL of a github repo or simple csv file containing list of URLs.

`-wp` working path: it is a path for cloning github repo.

`-l` log: giving this option lets it leave a log file at` data` directory.


If you want to run the pyszz separately (settings are different from the original) use following command
```
python3 main.py /path/to/bug-fixes.json /path/to/configuration-file.yml /path/to/repo-directory
```
