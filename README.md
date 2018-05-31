# ArtificialLife-Environment
UNAL 2018_I - Artificial Life Project


## Description
Artificial environment developed as final project for Artificial life course at Universidad Nacional de Colombia

### Components
#### Living Beings
Modeled agents correspond to fishes.
There are two species coexisting at the environment: preys and predators
Both of them have features as
 - Speed 
 - Metabolism rate
 - Vision range*(preys only)
 
Preys tend to need more food and predators can become inactive for periods when they reach a satisfaction threshold.
Their behavior is determined by simple rules.
For preys:
 - Flock motion
 - Escape from predators
 - Seek food
 
For predators:
 - Hunt when the current energy level is under satisfaction level
 - Be quiet otherwise

#### Plants
Plants represent food sources for preys. They're build using L-Systems

### Seasons
Food sources have several states which represent seasons (different growing stages depending on location). This feature lets us see interesting emergent behavior as migrations.

### Affine transformations
Each fish 
In this case the applied transformation corresponds to a non-linear transformation which tries to mimic *fisheye* distortion

### Turing patterns  - Reaction diffusion systems
One of preys' features is skin. 

Each skin correspond to a cellular automata which helps to model a reaction diffusion system.
Parameters are set based on Gray-Scott Model. Available at: http://mrob.com/pub/comp/xmorphia/

### Evolution
A genetic algorithm is performed, entire population is replaced using selection, reproduction, crossover and mutation principles

## Prerequisites
Java and processing are required to run this program

## Demo
*Insert video url here*
