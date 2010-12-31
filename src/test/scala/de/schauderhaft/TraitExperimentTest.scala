package de.schauderhaft
import org.scalatest._

class TraitExperimentTest extends FeatureSpec {

    feature("A usser can call go ") {
        scenario("without mocking") {
            new DoSomething().go
        }
        scenario("with mocking") {
            val something = new DoSomething() with MockTraitService
            something.go
        }
    }
}

class DoSomething extends TraitService {
    def go = doSomething
    def go2 = println ("I'm myself")
}

trait TraitService {
    def doSomething {
        println("I am the Trait")
    }
}

trait MockTraitService extends TraitService {
    override def doSomething {
        println("Hi, I am the Mock")
    }
}