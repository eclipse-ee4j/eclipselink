#!/bin/bash
diff <(zipinfo -1 $1 | sort) <(zipinfo -1 $2 | sort)