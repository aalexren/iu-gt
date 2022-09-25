# Report on the first practice test on the Game Theory course

*Author: Artem Chernitsa, 09.03.2000, BAI-01*  
*Date: 25th of September 2022*  
*Instructor: Professor N.Shilov*  

## 1. Idea
In this test, the work is carried out with the following concepts: Finite Position Games (FPG), Knaster-Tarski fix-point theorem, backward induction and solution of FPG's. With the given parametrized task supposed to write a program to play FPG and propose a winning strategy for both players in the game.  

Given `YEAR`, `MONTH` and `DAY` the game is following: start position is chosen in the range from 1 to `YEAR + MONTH + DAY`, move is in the range from 1 to `MONTH + DAY`. Players called *Duplicator* and *Spoiler*, take turn one after another and the winner is one who reach the `YEAR + MONTH + DAY` value.

To find the optimal startegy we simply use backward induction to figure out the set of all possible acceptable positions. As was mentioned on the lecture winning strategy in position $p$ iff either the position:
- final position,
- there exists move that to leads to a position $q$ where player has a winning position,
- every move of opponent from $p$ always leads to a winning position for player.

Backward induction simply says we will choose option and the both, player and opponent, will know every dicision and make their own decisions on the every step with revealed information.

## 2. Realisation

So, algorithm of constructing the set of the winning positions is the following: we will enumerate all psitions and mark every position that leads to position where we can achieve final state as False recursively. In that case recursion has been replaced by iterative approach.

- $start$ is start position,
- $stop$ is the final position,
- $step$ is the maximum value from range of the move
- $temp$ is set in the form of array (list)

```python
start = self.game_range[0]
stop = self.game_range[-1]
step = self.step_range[-1]

temp = [True] * (stop + 1)
for i in range(stop, start - 1, -(step + 1)):
    temp[i] = False
temp[stop] = True

return temp
```

## 3. How to run

Be sure to use `python 3.10.6` or higher. Following steps help to run project:
1. `git clone git@github.com:aalexren/iu-gt.git`
2. `cd Assignment_01`
3. `pip install --upgrade pip`
4. `python3 -m venv env`
5. `source env/bin/activate`
6. `pip install -r requirements.txt`
7. `python3 main.py`

If you use version of python script from the moodle uploads then simply do:
1. `pip install --upgrade pip`
2. `pip install click`
3. `python3 main.py`