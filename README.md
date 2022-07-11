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
pip3 install --no-cache-dir -r requirements.txt
```
---
## Usage

To run the tool, simply execute the following command:

```
python3 main.py /path/to/bug-fixes.json /path/to/configuration-file.yml /path/to/repo-directory
```