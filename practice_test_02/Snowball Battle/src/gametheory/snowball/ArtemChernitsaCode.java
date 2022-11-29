package gametheory.snowball;

public class ArtemChernitsaCode implements Player {

    private int time;
    private int shootToOpponentNumber; // how many throw to opponent field
    private int shootToHotNumber; // how many throw to hot field
    private int opponentHits; // how many times opponent throw snowballs to our field

    public ArtemChernitsaCode() {
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
            // this.time == 57 && minutesPassedAfterYourShot == 5 ||
            // this.time != 56 && minutesPassedAfterYourShot == 4 ||
            minutesPassedAfterYourShot == 4 ||
            this.time == 60
            )
        )
        {
            snowballsToThrow = 0;
        }


        if (opponentLastShotToYourField > 0) {
            opponentHits += 2;
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
        return "a.chernitsa@innopolis.university";
    }
    
}
