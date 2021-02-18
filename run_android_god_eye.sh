#!/bin/bash
adb forward tcp:5390 tcp:5390
open -a "Google Chrome" http://localhost:5390/index.html