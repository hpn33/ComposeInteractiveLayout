# ComposeInteractiveLayout

State: [**IN DEVELOPMENT**]

Description:
a InteractiveLayout to **move** and **zoom**

## how draw world

from compose world to simulated graphic draw world


> first layer is board -> to get size
>
> next layer is viewport -> to unwrap things and set scale
>
> last layer is world -> to set offset and set things

### order

- first: world on compose
    - set box to full size
    - because: get size of screen

- second:
    - to unwrapSize
    - set scale

- third world
    - set offset of world
    - set things on this

---


## TODO

- [x] main functionality
    - [x] move
    - [x] zoom
    - [x] calculate position
- [ ] optimize for more world obj
- [ ] make example
    - [x] graph
    - [ ] flowchart
    - [x] map

