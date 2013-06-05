scalaz7-validation-example
==========================

An interactive strawman proposal for a way we might consider using 
Scalaz 7 validations with the new, less-haskell-oriented syntax.

Read the [strawman code proposal](strawman.scala).


Console Usage
-------------
 
Assumptions:
  1. You've cloned this git repo, and are in the directory of the repo
  2. You've installed [sbt-extras](https://github.com/paulp/sbt-extras) as `sbt-extras.sh`
  

Just type:

    cat load_file_in_sbt - | sbt-extras.sh console-quick
