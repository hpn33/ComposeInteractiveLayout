# ComposeInteractiveLayout

State: [**IN DEVELOPMENT**]

Description:
a InteractiveLayout to **move** and **zoom** by simulating draw view and camera view

- box/border
    - draw layout
        - world
        - camera/viewport ?!?


- mouse position
    - on Screen
    - on world
    - [ ] on viewport

> Layout Order
> - box/border > draw layout

> NOTE:
>
> if scale be set on the top box when box go out of show area the child not be shown
>
> so scale and offset should be set on child/item of draw area