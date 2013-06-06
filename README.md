scalaz7-validation-example
==========================

An interactive strawman proposal for a way we might consider using 
Scalaz 7 validations with the new, less-haskell-oriented syntax.

Proposed benefits of this syntax:
  * no starship operators **<*>** nor **|@|**, etc
  * normal function application order: **f(x, y, z)**
  * one-liner to wrap any function to handle validated input and output

Read the [strawman code proposal](strawman.scala).


Console Usage
-------------

This strawman is all set up for you to load it in the Scala console and play with it.

Assumptions:
  1. You've cloned this git repo, and are in the directory of the repo
  2. You've installed [sbt-extras](https://github.com/paulp/sbt-extras) as `sbt-extras.sh` (note: this is already puppeted to all TIM Group developers)
  

Just type:

    cat load_file_in_sbt - | sbt-extras.sh console-quick
