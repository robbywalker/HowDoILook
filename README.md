How Do I Look?
========================

Cross platform web application look and feel tests.


Plans
========================

In the short term, I want to really nail down a robust two-way communication mechanism between the browser and the Java code. Obviously, Java can now send 'messages' to the browser, via the Robot. But, I don't really have a consistent way to pass data/results/etc back from th browser to the Robotron.

In the longer term, I'd like to create a DSL for describing actions for Robotron, that are then compiled to Javascript/keystrokes/mouse movements, and executed in browser. Then you can simply describe a test-suite in an external file, which is loaded and run by the Robotron. It seems like a good task to sharpen my Antlr skills on too.

Off the top of my head I'm imagining a DSL that looks something like this:
> visit http://greplin.com
> verify $(a[href=/jobs]) exists
> screenshot as homepage
> click $(a[href=/jobs])
> verify page doesn't contain "ninja"
> verify page doesnt contain "synergy"
> screenshot as jobs


Credits
--------------

Shaneal Manek

Greplin

http://code.google.com/p/pypng/
