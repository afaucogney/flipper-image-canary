Under developpenent



Oom

Oom is definitively something you do not want ot ovebserve as a app developper. App becone easy to develop, but it is easy to increase the app ram memry, because of bitmap.

without bitmap, layout is simple, and event big leak would become less important, because remanent vie would not take too much of memory (devil advocate)

The real life, shopw taht every app page is stuff by image so bipmap

- first insufre that view are not retrian because of leak (got to leak canary)
- insure your images get the good size :
    - scale type
    - ..
    - too big size
    - crop
 - ensure there is no bitmap in memory for nothing
   - are invisible
   - are not part of layout 




To do

- [ ] is the good format
- [ ] find the resource
- [ ] is there any bitmap in memory not in layout 

Describe solution





Contribution

Age