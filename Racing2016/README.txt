//**RACING_2016 Version 0.16 README.txt**//

- This is a project started by Pierre Theo Klein to test the applications of genetic algorithms for pathfinding. 


GENETICS:

- The cars have DNA that has of 10 codons, which in turn stores a possible move (1 = forward, 2 = backward, 3 = left, and 4 = right) as well as the desired time to make the move. 

- After every single car has completed their moves, they are ranked based off of a scoring algorithm, and the worst cars are killed off. 

- The scoring algorithm is: y = (100,000 / x) - t, where x is the distance from the parking space. Reasoning: we don’t care about the difference in distance of two terribly scoring cars (where both x-es are high), but we do care about minor differences in the distance of cars that are close to the goal (where both x-es are low, therefore a smaller ∆x = a greater ∆ score). t is the time spent on grass; this is because we want to penalize being in the grass as much as possible. A good car stays on asphalt. 

- The best cars breed together and create new progeny. Each progeny contains a mixture of each parent’s DNA. 

- The probability for mutation is relatively high, at 10% 

ENVIRONMENT:

- Grass is sticky like glue (so much so, that cars can no longer move). Asphalt has a lower friction than grass (cars can move on asphalt). 

CAR:

- Cars have default stats given to them. Here are the stats:
	- max speed: 200 px / second
	- max reverse: 100 px / second
	- max acceleration: 2 px / second^2
	- max brake: -2 px / second^2
 
NOTES:
-You must run “MainEvolution.java” once you compile all other .java files.