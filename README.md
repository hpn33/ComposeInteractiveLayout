# ComposeInteractiveLayout

State: [**IN DEVELOPMENT**]

Description:
a InteractiveLayout to **move** and **zoom** by simulating draw view and camera view

## how draw world

from compose world to simulated graphic draw world
> maybe im wrong: because can draw things with canvas and done
> but i want the power of compose
>
> the only thing that move is world ( by offset )
> and for zooming world position and scale is change by camera scale
>

### order

- first: world on compose
    - set box to full size
    - because: show size is set by screen

- second: viewport to offset world position with that
    - because: we need a point to calculate with that move and zoom

- third zooming
    - ???
    - is little need triangle math, but maybe

---

- box/border
    - draw layout
        - world
        - camera/viewport ?!?


- mouse position
    - on Screen
    - on world
    - [ ] on viewport

world saw by viewport

- viewport
- world
- entity ( stuff on world )

### plan

- [x] set world by viewport
- [x] set items to world ( in future is be depend on viewport and world [because the layout is viewport oriented])
-

> Layout Order
> - box/border > draw layout

> NOTE:
>
> if scale be set on the top box when box go out of show area the child not be shown
>
> so scale and offset should be set on child/item of draw area

## TODO

- [x] main functionality
    - [x] move
    - [x] zoom
    - [x] calculate position
- [ ] Refactor: make split and clean
    - code layout for InteractiveArea
- [ ] optimize for more world obj
- [ ] make example
    - [ ] graph
    - [ ] flowchart
    - [ ] map

