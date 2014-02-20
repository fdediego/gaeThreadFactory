gaeThreadFactory
================

Note: You will have to change your GAE location near the top of the pom.xml but that is all.

  Existing code:

I wrote a simple queue, that can have an item (which I called an Rfq) added to it, and the queue is polled every so often, to see if that item is there. If the Rfq has been around longer than a certain time, do something (like remove it).

Its really simple (but the implementation is, well….its java).

Now I know I could use a TaskQueue, but, I want to find out why I have a certain issue here.

There is a single unit test, that does nothing more than
 1) Start the environment
 2) Autowire the queue for testing
 3) start the queue up, for listening
 4) run the dummy test (which does nothing)
 5) stop, which causes the queue to shutdown

The unit test passes, but something odd happens in the console. 
