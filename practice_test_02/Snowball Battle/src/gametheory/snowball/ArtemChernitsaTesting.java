package gametheory.snowball;

import java.util.ArrayList;
import java.util.Random;

public class ArtemChernitsaTesting {
    
    private ArrayList<Agent> agents;
    private Random rand;

    public ArtemChernitsaTesting() {
        // Agent aOne = new Agent(new AggressiveAgent());
        // Agent aTwo = new Agent(new NaiveAgent());

        // playMatch(aOne, aTwo);

        agents = new ArrayList<>();

        rand = new Random();

        generatePlayers();
        runTournament();
        showResults();
    }

    private void showResults() {
        agents.sort((o1, o2) -> o1.score - o2.score);
        for (int i = 0; i < agents.size(); ++i) {
            System.out.printf("%d, %s, score: %d\n", i, agents.get(i).agent.getEmail(), agents.get(i).score);
        }
    }

    /*
     * Each play with each, except itself.
     */
    private void runTournament() {
        for (int i = 0; i < agents.size(); ++i) {
            for (int j = 0; j < agents.size(); ++j) {
                if (i != j) {
                    playMatch(agents.get(i), agents.get(j));
                }
                agents.get(i).agent.reset();
                agents.get(j).agent.reset();
            }
        }
    }

    private void generatePlayers() {
        for (int i = 0; i < 8; ++i) {
            int player = rand.nextInt(4)%3 + 1;
            // if (player == 0) {
            //     agents.add(new Agent(new RandomAgent()));
            // }
            if (player == 1) {
                agents.add(new Agent(new OptimalAgent()));
            }
            if (i < 3) { //player == 2 || i < 4) {
                agents.add(new Agent(new AggressiveAgent()));
            }
            if (player == 3) {
                agents.add(new Agent(new NaiveAgent()));
            }
        }
    }

    class Agent {
        public Player agent;
        public int score;
    
        public Agent(Player player) {
            this.agent = player;
            this.score = 0;
        }
    }

    public int maxSnowballsPerMinute(int minutesPassedAfterYourShot) {
        double exp = Math.exp(minutesPassedAfterYourShot);
        return (int) (15 * exp / (15 + exp));
    }

    /*
     * Play match between two agents.
     * Sixty rounds, results accumulated in agent's fields.
     */
    public void playMatch(Agent alhs, Agent arhs) {
        Player playerOne = alhs.agent;
        int playerOneOpponent = 0;
        int playerOneSnowballs = 100;
        int playerOneMinutesPassed = 0;

        Player playerTwo = arhs.agent;
        int playerTwoOpponent = 0;
        int playerTwoSnowballs = 100;
        int playerTwoMinutesPassed = 0;

        for (int i = 1; i <= 60; i++) {
            int playerOneOpponentNext = playerOne.shootToOpponentField(playerTwoOpponent, playerOneSnowballs, playerOneMinutesPassed);
            int playerOneHotNext = playerOne.shootToHotField(playerTwoOpponent, playerOneSnowballs, playerOneMinutesPassed);

            if (playerOneHotNext > 0 || playerOneOpponentNext > 0)
                playerOneMinutesPassed = 0;
            playerOneSnowballs = playerOneSnowballs - playerOneHotNext - playerOneOpponentNext;

            int playerTwoOpponentNext = playerTwo.shootToOpponentField(playerOneOpponent, playerTwoSnowballs, playerTwoMinutesPassed);
            int playerTwoHotNext = playerTwo.shootToHotField(playerOneOpponent, playerTwoSnowballs, playerTwoMinutesPassed);

            if (playerTwoHotNext > 0 || playerTwoOpponentNext > 0)
                playerTwoMinutesPassed = 0;
            playerTwoSnowballs = playerTwoSnowballs - playerTwoHotNext - playerTwoOpponentNext;

            playerOneOpponent = playerOneOpponentNext;
            playerOneSnowballs += playerTwoOpponentNext;
            playerOneMinutesPassed += 1;
            playerOneSnowballs += 1;
            
            playerTwoOpponent = playerTwoOpponentNext;
            playerTwoSnowballs += playerOneOpponentNext;
            playerTwoMinutesPassed += 1;
            playerTwoSnowballs += 1;
        }

        alhs.score += playerOneSnowballs;
        arhs.score += playerTwoSnowballs;

       System.out.printf("Player %s: %d, Player %s: %d", playerOne.getEmail(), alhs.score, playerTwo.getEmail(), arhs.score);
    }
}


class OptimalAgent implements Player {

    private int time;
    private int shootToOpponentNumber; // how many throw to opponent field
    private int shootToHotNumber; // how many throw to hot field
    private int opponentHits; // how many times opponent throw snowballs to our field

    public OptimalAgent() {
        this.time = 1;

        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;
        this.opponentHits = 0;
    }

    public void executeStrategy(
        int opponentLastShotToYourField,
        int snowballNumber,
        int minutesPassedAfterYourShot) {
        
        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;

        int snowballsLimit = this.maxSnowballsPerMinute(minutesPassedAfterYourShot);
        int snowballsToThrow = 0;
        if (snowballNumber >= snowballsLimit) {
            snowballsToThrow = snowballsLimit;
        }
        else {
            snowballsToThrow = snowballNumber;
        }

        if (!
            (this.time == 4 && minutesPassedAfterYourShot == 3 || 
            minutesPassedAfterYourShot == 4 ||
            time == 60
            )
        )
        {
            snowballsToThrow = 0;
        }


        if (opponentLastShotToYourField > 0) {
            this.opponentHits += 2;
        }

        if ((this.opponentHits > 0 && snowballsToThrow > 0) || this.time == 60) {
            this.shootToOpponentNumber = snowballsToThrow;
            this.opponentHits -= 1;
        }
        else {
            this.shootToHotNumber = snowballsToThrow;
        }
    }

    @Override
    public void reset() {
        this.time = 1;

        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;
        this.opponentHits = 0;
    }

    @Override
    public int shootToOpponentField(int opponentLastShotToYourField, int snowballNumber,
            int minutesPassedAfterYourShot) {
        
        this.executeStrategy(opponentLastShotToYourField, snowballNumber, minutesPassedAfterYourShot);
        
        return this.shootToOpponentNumber;
    }

    @Override
    public int shootToHotField(int opponentLastShotToYourField, int snowballNumber,
            int minutesPassedAfterYourShot) {
        
        
        this.time += 1; // since this method executed last

        return this.shootToHotNumber;
    }

    @Override
    public String getEmail() {
        return "Optimal Agent";
    }
    
}

class AggressiveAgent implements Player {

    private int time;
    private int shootToOpponentNumber; // how many throw to opponent field
    private int shootToHotNumber; // how many throw to hot field

    public AggressiveAgent() {
        this.time = 1;

        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;
    }

    /*
     * Always throws to opponent field.
     */
    public void executeStrategy(
        int opponentLastShotToYourField,
        int snowballNumber,
        int minutesPassedAfterYourShot) {
        
        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;

        int snowballsLimit = this.maxSnowballsPerMinute(minutesPassedAfterYourShot);
        int snowballsToThrow = 0;
        if (snowballNumber >= snowballsLimit) {
            snowballsToThrow = snowballsLimit;
        }
        else {
            snowballsToThrow = snowballNumber;
        }

        if (!
            (this.time == 4 && minutesPassedAfterYourShot == 3 ||
            minutesPassedAfterYourShot == 4 ||
            this.time == 60
            )
        )
        {
            snowballsToThrow = 0;
        }


        this.shootToOpponentNumber = snowballsToThrow;
    }

    @Override
    public void reset() {
        this.time = 1;

        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;
    }

    @Override
    public int shootToOpponentField(int opponentLastShotToYourField, int snowballNumber,
            int minutesPassedAfterYourShot) {
        this.executeStrategy(opponentLastShotToYourField, snowballNumber, minutesPassedAfterYourShot);
        
        return this.shootToOpponentNumber;
    }

    @Override
    public int shootToHotField(int opponentLastShotToYourField, int snowballNumber, int minutesPassedAfterYourShot) {
        this.time += 1; // since this method executed last

        return this.shootToHotNumber;
    }

    @Override
    public String getEmail() {
        return "Aggresive Agent";
    }
}

class NaiveAgent implements Player {

    private int time;
    private int shootToOpponentNumber; // how many throw to opponent field
    private int shootToHotNumber; // how many throw to hot field

    public NaiveAgent() {
        this.time = 1;

        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;
    }

    /*
     * All the time throws to HotField.
     */
    public void executeStrategy(
        int opponentLastShotToYourField,
        int snowballNumber,
        int minutesPassedAfterYourShot) {
        
        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;

        int snowballsLimit = this.maxSnowballsPerMinute(minutesPassedAfterYourShot);
        int snowballsToThrow = 0;
        if (snowballNumber >= snowballsLimit) {
            snowballsToThrow = snowballsLimit;
        }
        else {
            snowballsToThrow = snowballNumber;
        }

        if (!
            (this.time == 4 && minutesPassedAfterYourShot == 3 || 
            minutesPassedAfterYourShot == 4
            )
        )
        {
            snowballsToThrow = 0;
        }

        this.shootToHotNumber = snowballsToThrow;
    }

    @Override
    public void reset() {
        this.time = 1;

        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;
    }

    @Override
    public int shootToOpponentField(int opponentLastShotToYourField, int snowballNumber,
            int minutesPassedAfterYourShot) {
        this.executeStrategy(opponentLastShotToYourField, snowballNumber, minutesPassedAfterYourShot);
        
        return this.shootToOpponentNumber;
    }

    @Override
    public int shootToHotField(int opponentLastShotToYourField, int snowballNumber, int minutesPassedAfterYourShot) {
        this.time += 1; // since this method executed last

        return this.shootToHotNumber;
    }

    @Override
    public String getEmail() {
        return "Naive Agent";
    }
}

class RandomAgent implements Player {

    private int shootToOpponentNumber; // how many throw to opponent field
    private int shootToHotNumber; // how many throw to hot field
    private Random rnd;

    public RandomAgent() {
        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;

        rnd = new Random();
    }

    /*
    First choose either to throw or not, then
    how many to opponent and to hot field.
     */
    public void executeStrategy(
        int opponentLastShotToYourField,
        int snowballNumber,
        int minutesPassedAfterYourShot) {
        
        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;

        if (rnd.nextInt(2) == 0) {
            int snowballsLimit = this.maxSnowballsPerMinute(minutesPassedAfterYourShot);
            int canThrow = 0;
            if (snowballNumber >= snowballsLimit) {
                canThrow = snowballsLimit;
            }
            else {
                canThrow = snowballNumber;
            }

            int toHot = rnd.nextInt(canThrow + 1);
            int toOpponent = canThrow - toHot;
            this.shootToHotNumber = toHot;
            this.shootToOpponentNumber = toOpponent;
        }
    }

    @Override
    public void reset() {
        this.shootToOpponentNumber = 0;
        this.shootToHotNumber = 0;
    }

    @Override
    public int shootToOpponentField(int opponentLastShotToYourField, int snowballNumber,
            int minutesPassedAfterYourShot) {
        this.executeStrategy(opponentLastShotToYourField, snowballNumber, minutesPassedAfterYourShot);

        return this.shootToOpponentNumber;
    }

    @Override
    public int shootToHotField(int opponentLastShotToYourField, int snowballNumber, int minutesPassedAfterYourShot) {
        return this.shootToHotNumber;
    }

    @Override
    public String getEmail() {
        return "Random Agent";
    }
    
}