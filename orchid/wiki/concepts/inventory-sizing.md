## Calculating

When adding items to an inventory, you need to specify an `index`. This index starts at **zero** and ends at a specific number. An inventory always has **9 columns** and can have **1 - 6 rows**. From this one can create a **matrix**:

```
/  |  1  2  3  4  5  6  7  8  9
-  |  -- -- -- -- -- -- -- -- --
1  |  0  1  2  3  4  5  6  7  8
2  |  9  10 11 12 13 14 15 16 17
3  |  18 19 20 21 22 23 24 25 26
4  |  27 28 29 30 31 32 33 34 35
5  |  36 37 38 39 40 41 42 43 44
6  |  45 46 47 48 49 50 51 52 53
```

The numbers outside is the number of the **column/row** and the numbers inside are the respective indexes.


