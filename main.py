"""
@author: Chernitsa Artem
@contact: a.chernitsa@innopolis.university
@version: 1.0.0

Date of birth: 09.03.2000
"""

import click

import logging
import random
from datetime import datetime

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
logging.basicConfig()

YEAR = 2000
MONTH = 3
DAY = 9


class Game:
    def __init__(self, *, 
                game_range: tuple[int, int]=(1, YEAR + MONTH + DAY), 
                step_range: tuple[int, int]=(1, MONTH + DAY),
                mode=1, start=1):
        """
        @param game_range: range of all possible positions inclusively
        @param step_range: range of every possible move inclusively
        @param mode: Game mode [smart, random, advisor]
        """
        self.game_range = range(game_range[0], game_range[1] + 1)
        self.step_range = range(step_range[0], step_range[1] + 1)
        self.mode = mode
        self.moves = []
        self.position = start
        self.winner = None # 'D' or 'S'
        self.status = False # game is over
        self.turn = [1, ['D', 'S']] # by default first move for the Duplicator


    def run(self):
        self.win_positions = self.backward_induction()
        while not self.is_finish():
            self.next_turn()
            if self.turn[1][self.turn[0]] == 'D':
                self.position += self.duplicator_move()
            else:
                self.position += self.spoiler_move()
            
        
        self.winner = self.turn[1][self.turn[0]]
        logger.info(f'Game over, winner is {self.winner}')


    def next_turn(self):
        self.turn = [(self.turn[0] + 1) % len(self.turn[1]), self.turn[1]]


    def stat(self):
        res = f'Game range: {self.game_range[0]}, Move range: {self.step_range[-1]}\n'
        res += f'Win positions: {list(enumerate(self.win_positions))}\n'
        res += f'Moves: {self.moves}'
        res += f'Winner is {self.winner}'
        return res


    def duplicator_move(self) -> int:
        """
        return the move (in steps) of the player
        """
        
        move = click.prompt('Enter your move', 
                            type=click.IntRange(self.step_range[0], self.step_range[-1]))
        logger.info(f'Duplicator moved to {self.position + move} => +{move}')
        self.moves.append(('D', f'{self.position + move}', f'{move}'))
        return move


    def spoiler_move(self) -> int:
        """
        return the move (in steps) of the computer
        """

        move = None
        if self.mode == 2:
            move = random.randint(self.step_range[0], self.step_range[-1])
        else:
            if self.win_positions[self.position]:
                try:
                    move = self.win_positions.index(False, self.position) - self.position
                except ValueError:
                    move = self.game_range[-1] - self.position
            else:
                move = 1
                
        
        logger.info(f'Spoiler moved to {self.position + move} => +{move}')
        self.moves.append(('S', f'{self.position + move}', f'{move}'))
        return move


    def is_finish(self):
        """
        check if the game is over
        """

        return self.position == self.game_range[-1]


    def backward_induction(self) -> list[bool]:
        start = self.game_range[0]
        stop = self.game_range[-1]
        step = self.step_range[-1]

        temp = [True] * (stop + 1)
        for i in range(stop, start - 1, -(step + 1)):
            temp[i] = False
        temp[stop] = True

        return temp


class Session:
    def __init__(self):
        pass


    def run(self):
        while True:
            try:
                mode = click.prompt('Please choose game mode:\n 1. Smart\n 2. Random\n 3. Advisor\n',
                                    type=click.IntRange(1, 3), prompt_suffix='>')
                start = click.prompt(f'Enter game start position from x to {YEAR + MONTH + DAY}',
                                        type=click.IntRange(1, YEAR + MONTH + DAY))
                game = Game(game_range=(1, YEAR + MONTH + DAY),
                            step_range=(1, MONTH + DAY),
                            mode=mode, start=start)
                game.run()
                
                save_ops = {'y', 'n'}
                save = click.prompt('Do you want to save game?', type=click.Choice(save_ops))
                if save == 'y':
                    with open(f'log-{datetime.now().strftime("%m-%d-%Y.%H:%M:%S")}.log', 'w') as log:
                        log.write(game.stat())
                        logger.info('Log has been witten.')

                next_game = click.prompt('Do you want to play next game?', type=click.Choice(save_ops))
                if next_game == 'n':
                    logger.info('Exit the game...')
                    break
            except KeyboardInterrupt as e:
                logger.error(e)
                exit(128)


if __name__ == '__main__':
    session = Session()
    session.run()