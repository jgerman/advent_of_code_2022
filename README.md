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
