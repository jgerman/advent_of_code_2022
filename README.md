# jgerman/advent_of_code_2022

Advent of code 2022. As usualy not going for speed, golfing, etc, just trying to
write clean code and solve the problems.

## Day 1

Obviously a nice easy slow start. Initially I went with max on the list of the
sum of the elves calories... I should have anticipated that I'd want them sorted
so I could grab a set of them. Quick and easy change.

## Day 2

I thought I was being slick by just turning the strategy guide into something
readable then basing my answer off of that, great for task 1, but iffy for task
2.

This could be solved more elegantly by passing how to handle the second column
of the guide in to be used at the last minute instead of where I'm currently
passing it, but I wanted to wrap up before my lunch break was over. One to
revisit and refactor to be clean.


## Day 3

Did this on day 4. First pass I assumed I'd need the frequencies for some
reason. Which still got me to the right answer but once I read the second task I
realized that the only difference was in how the groups of rucksacks to test
were made. And that's the function that gets passed to the algorithm: group each
rucksack into compartments, or group every three rucksacks together.


## Day 4

Also did this on day 4, got cute with the first check because I assumed the
ranges would be large and turning them into sets was going to churn, wound up
re-writing to just use sets once I saw the second task.


## Day 5

Knocked this out on day 6, so still a day behind. A little compulsive
refactoring to pass the crate-mover model along after the fact but super easy.
Most of the work is getting the data into a shape I want to work with.

## Day 6

This was a 5 minute task. A sliding window with a configureable window size
handled it easily. I made the window configureable from the start guessing that
that'd be necessary in the second. I swear we had something extremely similar
last year.

## Day 7

Another easy one and another one where a stack makes the solution pretty
trivial. Rather than build out a tree I only handled directory changes (pushing
new dirs on the stack with a uuid in case there were nested dirs... which there
were), and file sizes.

Maintaining a running list of directory sizes by looking at the current
directory stack was trivial.

Finding the space needed was just arithmetic given the sizes.


## Day 8

I had the idea right, but this is sloppy. Visibility and score are basically the
same but I just re-implemented the basic flow for score. Additionally I was
wrong at first because I didn't think to reverse the "before" lists, then a
second time because I didn't think through my use of take-while. What I really
wanted was take-until. I discovered halt-when after I solved the problem so that
bit should be pretty easy to re-write.

## Day 9

I didn't re-factor this as well as I should have for part 2. I'm not sure
there's a need to keep the head separate from the 'tails' just applying the move
to the first position should then passing the whole rope to propogate moves
would be cleaner.

Again manipulating the input was key. Calculating moves are a lot easier when
the head moves one step at a time so expanding the input was key.

Small setback on knots higher than 2 for part 2 because I wasn't thinking about
conj order and was using a list where a vector would give me the order I wanted.

## Day 10

Not a hard one yet. It could probably be more efficient but the answer returns
immediately so good enough.

I realized I didn't need to track the value at every time t so I just tracked
the state changes and wrote a lookup function to figure out what X was based on
that.

For part 2 it took me a second to realize that the fact that the pixels start at
0 and the cycles at 1 actually makes a difference. Initially I thought they were
the same. Which lead to the second slow down... I still kept the values linked
and forgot to mod the lookup at < 40 instead of <= 40. Which actually only
resulted in a single pixel off which seemed really weird until I saw way (though
a one pixel difference wasn't a problem in getting the solution in).

I think I could clean this up to be more readable, especially around variable
names... I mixed the time var and the pixel vars in a way I probably shouldn't
have.


## Day 11

Brute force worked on task 1. Task 2 failed with an integer overflow, which was
obviously the trick, how to represent the numbers. Took me a awhile to think
through but the big clue was all the divisors were primes. If you just tracked
the remainder for each potential divisor you can keep to low numbers. Should
work just fine for non-primes too but seeing those primes just pushed me in the
direction of thinking in "atomic" values.

I'm sure there's a clever way to do it I missed, but I got the answer. Probably
could use some optimization and task 1 needs to be re-written to work with task
2 but that should be fairly straight-forward.

First class functions were the hero today. I changed the items from being
primitive ints to a list of maps and the bulk of the code didn't need changing.
Each monkey was defined in terms of the math it did when inspecting and the
predicate it used to choose who it throw to and that all just continued to work.


## Day 12

Work... football... missed it on the day. Was just going to lag a day but looked
at day 13 and saw how neatly that fit into a clojure solution and knocked that
out instead.

This does look like a pathing problem but to be continued...

Came back to this on day 17, I'm way behind. I took the time to implement my own
BFS since it's been awhile. It looked like it was super slow until I realized I
left an errant println in place. Doesn't mean I can't clean it up but that ns
should be useable from here on out and I didn't go to an external library, which
would have made this trivial.

Best bet is to come back and clean it up for use next year.


## Day 13

As soon as I saw this I knew it was going to be a breeze. The data was already
nearly EDN, though I did wind up swapping braces for parens for ease of conjing
a value back onto the front of the list.

The one thing I'm kicking myself for is not realizing that I was implementing a
comparator from the jump. In the end all I had to do was search and replace the
keywords I was using with -1, 0, 1 and the process lists could be passed as a
comparator to sort though... just a few minutes and task 2 was done.

I suppose I could come back to this and implement my own sorting algorithm at
some point.

## Day 14

Finally got around to doing day 14, it's Dec 30. The way I implemented the first
task the simulation stops once a grain falls past the lowest floor. That grain
is not placed.

I could solve the second task but re-implementing the conditions for stopping
but I think I can tweak things by changing the stop condition, which will
require placing grains that stop falling.

So all it took was to add the ability to pass in the stop condition and to place
sand that reached the max y value. Calculating the answer isn't fast but it
works.

This is one to come back to for performance reasons.


## Day 15

Started this on 12/31/2022. The naive solution (getting the bounded points for
every beacon and filterin out all those not belonging to the row in question)
simply takes far too long. There's clearly a trick.

I did decide to use instaparse on this. The input could easily have been
converted with string operations but having a parser as part of your toolkit is
valuable imo and it's been awhile since I pulled out instaparse. Even though it
took a little longer the code is cleaner (imo) than making a bunch of splits and
substitutions. I may start using it more to make sure I stay fluent with that
library.

My first optimization was to eliminate all of the input rows that couldn't
possibly affect the row I was after. That wasn't enough.

Optimization two was to cheat a bit... There aren't many cases where I'd usually
do this in real code, but I defined a dynamic var that could be set to a row and
all ranges would be restricted to that row only. I generally prefer to provide
general-ish solutions but in this case since it was task 1 I wanted to see what
task two wanted quickly to see if that optimzation would hold up. Looking at
part two I don't think it will. It might finish but it would certainly be
several minutes. If it were less than a minute I'd be ok with itbut several
means there has to be a better way.

I suspect there are interactions between either each beacon, or a beacon and
other sensors, or some permutation there that's exploitable. Checking in task 1
and thinking it over a bit...
