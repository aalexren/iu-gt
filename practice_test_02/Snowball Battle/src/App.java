import gametheory.snowball.ArtemChernitsaCode;
import gametheory.snowball.ArtemChernitsaTesting;
import gametheory.snowball.Player;

class Agent implements Comparable<Agent> {
    public Player agent;
    public int totalScore;

    public Agent(Player someAgent) {
        agent = someAgent;
        totalScore = 0;
    }

    @Override
    public int compareTo(Agent o) {
        return this.totalScore - o.totalScore;
    }
}

public class App {
    public static void main(String[] args) throws Exception {
        // Agent a1 = new Agent(new ArtemChernitsaCode());
        // Agent a2 = new Agent(new ArtemChernitsaCode());
        
        ArtemChernitsaTesting test = new ArtemChernitsaTesting();
        // test.playMatch(a1, a2);
        // conductSnowBattle(a1, a2);
        // for (int i = 0; i < 15; i += 1) {
        //     System.out.println(maxSnowballsPerMinute(i));
        // }
    }

    public static int maxSnowballsPerMinute(int minutesPassedAfterYourShot) {
        double exp = Math.exp(minutesPassedAfterYourShot);
        return (int) (15 * exp / (15 + exp));
    }

    public static void conductSnowBattle(Agent a1, Agent a2) {
        Player p1 = a1.agent;
        Player p2 = a2.agent;
        p1.reset();
        p2.reset();
        int p1Snowballs = 100;
        int p2Snowballs = 100;
        int p1Minutes = 0;
        int p2Minutes = 0;
        int p1Opponent = 0;
        int p2Opponent = 0;
        for (int i = 1; i <= 60; i++) {
            int p1OpponentTemp = p1.shootToOpponentField(p2Opponent, p1Snowballs, p1Minutes);
            int p1HotTemp = p1.shootToHotField(p2Opponent, p1Snowballs, p1Minutes);
            System.out.printf("Round %d - hot = %d, opponent = %d, snowballs = %d\n", i, p1HotTemp, p1OpponentTemp, p1Snowballs);

            if (p1HotTemp + p1OpponentTemp > maxSnowballsPerMinute(p1Minutes))
                System.out.println("Error");

            if (p1HotTemp > 0 || p1OpponentTemp > 0)
                p1Minutes = 0;
            p1Snowballs = p1Snowballs - p1HotTemp - p1OpponentTemp;

            int p2OpponentTemp = p2.shootToOpponentField(p1Opponent, p2Snowballs, p2Minutes);
            int p2HotTemp = p2.shootToHotField(p1Opponent, p2Snowballs, p2Minutes);

            if (p2HotTemp + p2OpponentTemp > maxSnowballsPerMinute(p2Minutes))
                System.out.println("Error");

            if (p2HotTemp > 0 || p2OpponentTemp > 0)
                p2Minutes = 0;
            p2Snowballs = p2Snowballs - p2HotTemp - p2OpponentTemp;

            p1Opponent = p1OpponentTemp;
            p2Opponent = p2OpponentTemp;

            p1Snowballs += p2Opponent;
            p2Snowballs += p1Opponent;

            p1Minutes++;
            p2Minutes++;
            p1Snowballs++;
            p2Snowballs++;
        }

        a1.totalScore += p1Snowballs;
        a2.totalScore += p2Snowballs;

       System.out.printf("Player 1 snowballs - %d, Player 2 snowballs - %d\n", p1Snowballs, p2Snowballs);
    }
}