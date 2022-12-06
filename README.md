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
