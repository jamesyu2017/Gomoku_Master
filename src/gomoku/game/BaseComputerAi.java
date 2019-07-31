/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gomoku.game;

/**
 *
 * @author yuhe
 */

import java.util.*;

//Key Algorithm:
// think one step further for both player and computer, to see whether it will change the situation or not
// combine all the result above including both
// according to rules to set scores and priorities for the result

public class BaseComputerAi extends BasePlayer {


    // four dimensions to consider, I'm using Chinese Pinyin here :D
    private static final int HENG = 0;
    private static final int ZHONG = 1;
    private static final int ZHENG_XIE = 2;
    private static final int FAN_XIE = 3;
    
    private static final boolean FORWARD = true;
    private static final boolean BACKWARD = false;

    //we choose only ALIVE(no block points in both side), and HALF_ALIVE(one block points), others are not considered
    private static final int ALIVE = 1;
    private static final int HALF_ALIVE = 0;


    //private static final int DEAD = -1;

    //would have problem if the range is too much
    private class CalcuteRange {
        int xStart, yStart, xStop, yStop;

        private CalcuteRange(int xStart, int yStart, int xStop, int yStop) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xStop = xStop;
            this.yStop = yStop;
        }
    }


    //think one step further
    private static final int RANGE_STEP = 1;
    CalcuteRange currentRange = new CalcuteRange(0, 0, 0, 0);

    private void initRange(List<Point> comuters, List<Point> humans) {
        currentRange.xStart = humans.get(0).getX() - RANGE_STEP;
        currentRange.yStart = humans.get(0).getY() - RANGE_STEP;
        currentRange.xStop = humans.get(0).getX() + RANGE_STEP;
        currentRange.yStop = humans.get(0).getY() + RANGE_STEP;
        for (Point point : humans) {
            if (point.getX() - RANGE_STEP < currentRange.xStart) {
                currentRange.xStart = point.getX() - RANGE_STEP;
            } else if (point.getX() + RANGE_STEP > currentRange.xStop) {
                currentRange.xStop = point.getX() + RANGE_STEP;
            }
            if (point.getY() - RANGE_STEP < currentRange.yStart) {
                currentRange.yStart = point.getY() - RANGE_STEP;
            } else if (point.getY() + RANGE_STEP > currentRange.yStop) {
                currentRange.yStop = point.getY() + RANGE_STEP;
            }
        }
        for (Point point : comuters) {
            if (point.getX() - RANGE_STEP < currentRange.xStart) {
                currentRange.xStart = point.getX() - RANGE_STEP;
            } else if (point.getX() + RANGE_STEP > currentRange.xStop) {
                currentRange.xStop = point.getX() + RANGE_STEP;
            }
            if (point.getY() - RANGE_STEP < currentRange.yStart) {
                currentRange.yStart = point.getY() - RANGE_STEP;
            } else if (point.getY() + RANGE_STEP > currentRange.yStop) {
                currentRange.yStop = point.getY() + RANGE_STEP;
            }
        }

        // if one step further and it out of the range of board, treat it as in the board
        currentRange.xStart = currentRange.xStart < 0 ? 0 : currentRange.xStart;
        currentRange.yStart = currentRange.yStart < 0 ? 0 : currentRange.yStart;
        currentRange.xStop = currentRange.xStop >= maxX ? maxX - 1 : currentRange.xStop;
        currentRange.yStop = currentRange.yStop >= maxY ? maxY - 1 : currentRange.yStop;
    }

    // Method to analysis and calculate distance
    private Point doAnalysis(List<Point> comuters, List<Point> humans) {
        //first step
        if (humans.size() == 1) {
            return getFirstPoint(humans);
        }

        // initialize calculation range
        initRange(comuters, humans);

        // clear result before
        initAnalysisResults();
        // Start analysing blank points
        Point bestPoint = doFirstAnalysis(comuters, humans);
        if (bestPoint != null) {
            //System.out.println("optimal points found");
            return bestPoint;
        }
        // Acoording to analyse result, get optimal points
        bestPoint = doComputerSencondAnalysis(computerFirstResults, computerSencodResults);
        if (bestPoint != null) {
            //System.out.println("optimal points found");
            return bestPoint;
        }
        computerFirstResults.clear();
        System.gc();
        // according to result to find points that might lose
        bestPoint = doHumanSencondAnalysis(humanFirstResults, humanSencodResults);
        if (bestPoint != null) {
            //System.out.println("you will lose if not placing in this");
            return bestPoint;
        }
        humanFirstResults.clear();
        System.gc();
        //if we didn't find a point that will directly win, do third analysis
        return doThirdAnalysis();
    }


    private static final HashMap<Integer, Integer> fMap = new HashMap<>();


    // the first point have no complex algorithm
    private Point getFirstPoint(List<Point> humans) {
        Point point = humans.get(0);
        if (myPoints.isEmpty()) {
            // human players place the first move
            return xm1(point);
        } else if (point.x == 6 && point.y == 6) {
            return new Point(6, 8);
        } else if (point.x == 6 && point.y == 7) {
            return new Point(6, 6);
        } else if (point.x == 6 && point.y == 8) {
            return new Point(6, 6);
        } else if (point.x == 7 && point.y == 6) {
            return new Point(8, 8);
        } else if (point.x == 7 && point.y == 8) {
            return new Point(6, 8);
        } else if (point.x == 8 && point.y == 6) {
            return new Point(6, 6);
        } else if (point.x == 8 && point.y == 7) {
            return new Point(6, 6);
        } else if (point.x == 8 && point.y == 8) {
            return new Point(6, 8);
        } else if (point.x == 5 && point.y == 5) {
            return new Point(6, 8);
        } else {
            return new Point(6, 6);
        }


    }

    private Point xm1(Point point) {
        if (point.getX() == 0 || point.getY() == 0 || point.getX() == maxX && point.getY() == maxY)
            return new Point(maxX / 2, maxY / 2);
        else {
            return new Point(point.getX() - 1, point.getY());
        }
    }

//  private int debugx,debugy;

    // start analysing
    private Point doFirstAnalysis(List<Point> comuters, List<Point> humans) {
        int size = allFreePoints.size();
        Point computerPoint = null;
        Point humanPoint = null;
        int x, y;
        FirstAnalysisResult firstAnalysisResult;
        for (int i = 0; i < size; i++) {
            computerPoint = allFreePoints.get(i);
            // should remember x and y value for further computation
            x = computerPoint.getX();
            y = computerPoint.getY();
            if (x < currentRange.xStart || x > currentRange.xStop || y < currentRange.yStart || y > currentRange.yStop) {
                continue;
            }

            // try to place in horizontal line, analyse result
            firstAnalysisResult = tryAndCountResult(comuters, humans, computerPoint, HENG);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult != null) {
                if (firstAnalysisResult.count == 5)
                    return computerPoint;
                addToFirstAnalysisResult(firstAnalysisResult, computerFirstResults);
            }

            // vertical analysis
            firstAnalysisResult = tryAndCountResult(comuters, humans, computerPoint, ZHONG);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult != null) {
                if (firstAnalysisResult.count == 5)
                    return computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult, computerFirstResults);
            }

            // diagonal
            firstAnalysisResult = tryAndCountResult(comuters, humans, computerPoint, ZHENG_XIE);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult != null) {
                if (firstAnalysisResult.count == 5)
                    return computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult, computerFirstResults);
            }

            // verse diagonal
            firstAnalysisResult = tryAndCountResult(comuters, humans, computerPoint, FAN_XIE);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult != null) {
                if (firstAnalysisResult.count == 5)
                    return computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult, computerFirstResults);
            }

            // consider enemy's move in horizontal
            firstAnalysisResult = tryAndCountResult(humans, comuters, computerPoint, HENG);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult != null) {
                if (firstAnalysisResult.count == 5)
                    humanPoint = computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult, humanFirstResults);
            }

            // vertical
            firstAnalysisResult = tryAndCountResult(humans, comuters, computerPoint, ZHONG);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult != null) {
                if (firstAnalysisResult.count == 5)
                    humanPoint = computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult, humanFirstResults);
            }

            // diagonal
            firstAnalysisResult = tryAndCountResult(humans, comuters, computerPoint, ZHENG_XIE);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult != null) {
                if (firstAnalysisResult.count == 5)
                    humanPoint = computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult, humanFirstResults);
            }

            // verse diagonal
            firstAnalysisResult = tryAndCountResult(humans, comuters, computerPoint, FAN_XIE);
            computerPoint.setX(x).setY(y);
            if (firstAnalysisResult != null) {
                if (firstAnalysisResult.count == 5)
                    humanPoint = computerPoint;

                addToFirstAnalysisResult(firstAnalysisResult, humanFirstResults);
            }
        }
        // if no winning points, no need to return optimal
        return humanPoint;
    }

    //second analysis，analysis the first result，first result will return 4 results in different directions（enemy and self are bothe 4）
    // treat 4 result as one 
    private Point doComputerSencondAnalysis(Map<Point, List<FirstAnalysisResult>> firstResults, List<SencondAnalysisResult> sencodResults) {
        List<FirstAnalysisResult> list = null;
        SencondAnalysisResult sr = null;
        for (Point p : firstResults.keySet()) {
            sr = new SencondAnalysisResult(p);
            list = firstResults.get(p);
            for (FirstAnalysisResult result : list) {
                if (result.count == 4) {
                    if (result.aliveState == ALIVE) {//  one step to win
                        return result.point;
                    } else {
                        sr.halfAlive4++;
                        computer4HalfAlives.add(sr);
                    }
                } else if (result.count == 3) {
                    if (result.aliveState == ALIVE) {
                        sr.alive3++;
                        if (sr.alive3 == 1) {
                            computer3Alives.add(sr);
                        } else {
                            computerDouble3Alives.add(sr);
                        }
                    } else {
                        sr.halfAlive3++;
                        computer3HalfAlives.add(sr);
                    }
                } else {
                    sr.alive2++;
                    if (sr.alive2 == 1) {
                        computer2Alives.add(sr);
                    } else {
                        computerDouble2Alives.add(sr);
                    }
                }
            }
            sencodResults.add(sr);
        }
        // didn't find optimal result
        return null;
    }

    //almost same as previous
    private Point doHumanSencondAnalysis(Map<Point, List<FirstAnalysisResult>> firstResults, List<SencondAnalysisResult> sencodResults) {
        List<FirstAnalysisResult> list = null;
        SencondAnalysisResult sr = null;
        for (Point p : firstResults.keySet()) {
            sr = new SencondAnalysisResult(p);
            list = firstResults.get(p);
            for (FirstAnalysisResult result : list) {
                if (result.count == 4) {
                    if (result.aliveState == ALIVE) {
                        human4Alives.add(sr);
                    } else {
                        sr.halfAlive4++;
                        human4HalfAlives.add(sr);
                    }
                } else if (result.count == 3) {
                    if (result.aliveState == ALIVE) {
                        sr.alive3++;
                        if (sr.alive3 == 1) {
                            human3Alives.add(sr);
                        } else {
                            humanDouble3Alives.add(sr);
                        }
                    } else {
                        sr.halfAlive3++;
                        human3HalfAlives.add(sr);
                    }
                } else {
                    sr.alive2++;
                    if (sr.alive2 == 1) {
                        human2Alives.add(sr);
                    } else {
                        humanDouble2Alives.add(sr);
                    }
                }
            }
            sencodResults.add(sr);
        }
        return null;
    }

    private void sleep(int miniSecond) {
        try {
            Thread.sleep(miniSecond);
        } catch (InterruptedException e) {
        }
    }


    private Point doThirdAnalysis() {
        if (!computer4HalfAlives.isEmpty()) {
            return computer4HalfAlives.get(0).point;
        }
        System.gc();
        sleep(300);
        Collections.sort(computerSencodResults);
        System.gc();
        
        Point mostBest = getBestPoint(human4Alives, computerSencodResults);
        if (mostBest != null)
            return mostBest;

        Collections.sort(humanSencodResults);
        System.gc();

        mostBest = getBestPoint();
        if (mostBest != null)
            return mostBest;

        return computerSencodResults.get(0).point;
    }

    // decide to attack or defend
    protected Point getBestPoint() {
        Point mostBest = getBestPoint(computerDouble3Alives, humanSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(computer3Alives, humanSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(humanDouble3Alives, computerSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(human3Alives, computerSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(computerDouble2Alives, humanSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(computer2Alives, humanSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(computer3HalfAlives, humanSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(human4HalfAlives, computerSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(humanDouble2Alives, computerSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(human2Alives, computerSencodResults);
        if (mostBest != null)
            return mostBest;

        mostBest = getBestPoint(human3HalfAlives, computerSencodResults);
        return mostBest;
    }


    // final result in third step
    protected Point getBestPoint(List<SencondAnalysisResult> myBest, List<SencondAnalysisResult> yourSencodResults) {
        if (!myBest.isEmpty()) {
            if (myBest.size() > 1) {
                for (SencondAnalysisResult your : yourSencodResults) {
                    if (myBest.contains(your)) {
                        return your.point;
                    }
                }
                return myBest.get(0).point;
            } else {
                return myBest.get(0).point;
            }
        }
        return null;
    }


    // first result AI
    private final Map<Point, List<FirstAnalysisResult>> computerFirstResults = new HashMap<Point, List<FirstAnalysisResult>>();
    private final Map<Point, List<FirstAnalysisResult>> humanFirstResults = new HashMap<Point, List<FirstAnalysisResult>>();
    // second result AI
    protected final List<SencondAnalysisResult> computerSencodResults = new ArrayList<SencondAnalysisResult>();
    protected final List<SencondAnalysisResult> humanSencodResults = new ArrayList<SencondAnalysisResult>();
    //secon result AI
    protected final List<SencondAnalysisResult> computer4HalfAlives = new ArrayList<SencondAnalysisResult>(2);
    protected final List<SencondAnalysisResult> computerDouble3Alives = new ArrayList<SencondAnalysisResult>(4);
    protected final List<SencondAnalysisResult> computer3Alives = new ArrayList<SencondAnalysisResult>(5);
    protected final List<SencondAnalysisResult> computerDouble2Alives = new ArrayList<SencondAnalysisResult>();
    protected final List<SencondAnalysisResult> computer2Alives = new ArrayList<SencondAnalysisResult>();
    protected final List<SencondAnalysisResult> computer3HalfAlives = new ArrayList<SencondAnalysisResult>();

    // second result human
    protected final List<SencondAnalysisResult> human4Alives = new ArrayList<SencondAnalysisResult>(2);
    protected final List<SencondAnalysisResult> human4HalfAlives = new ArrayList<SencondAnalysisResult>(5);
    protected final List<SencondAnalysisResult> humanDouble3Alives = new ArrayList<SencondAnalysisResult>(2);
    protected final List<SencondAnalysisResult> human3Alives = new ArrayList<SencondAnalysisResult>(10);
    protected final List<SencondAnalysisResult> humanDouble2Alives = new ArrayList<SencondAnalysisResult>(3);
    protected final List<SencondAnalysisResult> human2Alives = new ArrayList<SencondAnalysisResult>();
    protected final List<SencondAnalysisResult> human3HalfAlives = new ArrayList<SencondAnalysisResult>();


    private void initAnalysisResults() {
        computerFirstResults.clear();
        humanFirstResults.clear();

        computerSencodResults.clear();
        humanSencodResults.clear();

        computer4HalfAlives.clear();
        computerDouble3Alives.clear();
        computer3Alives.clear();
        computerDouble2Alives.clear();
        computer2Alives.clear();
        computer3HalfAlives.clear();

        human4Alives.clear();
        human4HalfAlives.clear();
        humanDouble3Alives.clear();
        human3Alives.clear();
        humanDouble2Alives.clear();
        human2Alives.clear();
        human3HalfAlives.clear();
        System.gc();
    }

    private void addToFirstAnalysisResult(FirstAnalysisResult result, Map<Point, List<FirstAnalysisResult>> dest) {
        if (dest.containsKey(result.point)) {
            dest.get(result.point).add(result);
        } else {
            List<FirstAnalysisResult> list = new ArrayList<FirstAnalysisResult>(1);
            list.add(result);
            dest.put(result.point, list);
        }
    }

    private class FirstAnalysisResult {
        int count;
        Point point;
        int direction;
        int aliveState;

        private FirstAnalysisResult(int count, Point point, int direction) {
            this(count, point, direction, ALIVE);
        }

        private FirstAnalysisResult(int count, Point point, int direction, int aliveState) {
            this.count = count;
            this.point = point;
            this.direction = direction;
            this.aliveState = aliveState;
        }


        private FirstAnalysisResult init(Point point, int direction, int aliveState) {
            this.count = 1;
            this.point = point;
            this.direction = direction;
            this.aliveState = aliveState;
            return this;
        }

        private FirstAnalysisResult cloneMe() {
            return new FirstAnalysisResult(count, point, direction, aliveState);
        }

    }

    class SencondAnalysisResult implements Comparable<SencondAnalysisResult> {
        int alive4 = 0;
        int alive3 = 0;
        int halfAlive4 = 0;
        int halfAlive3 = 0;
        int alive2 = 0;
        Point point;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((point == null) ? 0 : point.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            SencondAnalysisResult other = (SencondAnalysisResult) obj;
            if (point == null) {
                if (other.point != null)
                    return false;
            } else if (!point.equals(other.point))
                return false;
            return true;
        }

        private SencondAnalysisResult(Point point) {
            this.point = point;
        }

        @Override
        public int compareTo(SencondAnalysisResult another) {
            return compareTowResult(this, another);
        }

    }

    private int compareTowResult(SencondAnalysisResult oneResult, SencondAnalysisResult another) {
        if (oneResult.alive4 > another.alive4) {
            return -1;
        }
        if (oneResult.alive4 < another.alive4) {
            return 1;
        }
        if (oneResult.halfAlive4 > another.halfAlive4) {
            return -1;
        }
        if (oneResult.halfAlive4 < another.halfAlive4) {
            return 1;
        }
        if (oneResult.alive3 > another.alive3) {
            return -1;
        }
        if (oneResult.alive3 < another.alive3) {
            return 1;
        }
        if (oneResult.alive2 > another.alive2) {
            return -1;
        }
        if (oneResult.alive2 < another.alive2) {
            return 1;
        }
        if (oneResult.halfAlive3 > another.halfAlive3) {
            return -1;
        }
        if (oneResult.halfAlive3 > another.halfAlive3) {
            return 1;
        }
        return 0;
    }

    private final FirstAnalysisResult far = new FirstAnalysisResult(1, null, HENG);
    
    private FirstAnalysisResult tryAndCountResult(List<Point> myPoints, List<Point> enemyPoints, Point point, int direction) {
        int x = point.getX();
        int y = point.getY();
        FirstAnalysisResult fr = null;

        int maxCountOnThisDirection = maxCountOnThisDirection(point, enemyPoints, direction, 1);
        if (maxCountOnThisDirection < 5) {
            return null;
        } else if (maxCountOnThisDirection == 5) {
            fr = far.init(point, direction, HALF_ALIVE);
        } else {
            
            fr = far.init(point, direction, ALIVE);
        }
        
        countPoint(myPoints, enemyPoints, point.setX(x).setY(y), fr, direction, FORWARD);
        countPoint(myPoints, enemyPoints, point.setX(x).setY(y), fr, direction, BACKWARD);


        if (fr.count <= 1 || (fr.count == 2 && fr.aliveState == HALF_ALIVE)) {
            return null;
        }
        return fr.cloneMe();
    }

    // points out of board
    private boolean isOutSideOfWall(Point point, int direction) {
        if (direction == HENG) {
            return point.getX() < 0 || point.getX() >= maxX;
        } else if (direction == ZHONG) {
            return point.getY() < 0 || point.getY() >= maxY;
        } else {
            return point.getX() < 0 || point.getY() < 0 || point.getX() >= maxX || point.getY() >= maxY;
        }
    }

    private Point pointToNext(Point point, int direction, boolean forward) {
        switch (direction) {
            case HENG:
                if (forward)
                    point.x++;
                else
                    point.x--;
                break;
            case ZHONG:
                if (forward)
                    point.y++;
                else
                    point.y--;
                break;
            case ZHENG_XIE:
                if (forward) {
                    point.x++;
                    point.y--;
                } else {
                    point.x--;
                    point.y++;
                }
                break;
            case FAN_XIE:
                if (forward) {
                    point.x++;
                    point.y++;
                } else {
                    point.x--;
                    point.y--;
                }
                break;
        }
        return point;
    }

    // calculate remaining points in 8 direction
    private void countPoint(List<Point> myPoints, List<Point> enemyPoints, Point point, FirstAnalysisResult fr, int direction, boolean forward) {
        if (myPoints.contains(pointToNext(point, direction, forward))) {
            fr.count++;
            if (myPoints.contains(pointToNext(point, direction, forward))) {
                fr.count++;
                if (myPoints.contains(pointToNext(point, direction, forward))) {
                    fr.count++;
                    if (myPoints.contains(pointToNext(point, direction, forward))) {
                        fr.count++;
                    } else if (enemyPoints.contains(point) || isOutSideOfWall(point, direction)) {
                        fr.aliveState = HALF_ALIVE;
                    }
                } else if (enemyPoints.contains(point) || isOutSideOfWall(point, direction)) {
                    fr.aliveState = HALF_ALIVE;
                }
            } else if (enemyPoints.contains(point) || isOutSideOfWall(point, direction)) {
                fr.aliveState = HALF_ALIVE;
            }
        } else if (enemyPoints.contains(point) || isOutSideOfWall(point, direction)) {
            fr.aliveState = HALF_ALIVE;
        }
    }


    // decide whether we could find 5 points in on direction
    private int maxCountOnThisDirection(Point point, List<Point> enemyPoints, int direction, int count) {
        int x = point.getX(), y = point.getY();
        switch (direction) {
            //horizontal
            case HENG:
                while (!enemyPoints.contains(point.setX(point.getX() - 1)) && point.getX() >= 0 && count < 6) {
                    count++;
                }
                point.setX(x);
                while (!enemyPoints.contains(point.setX(point.getX() + 1)) && point.getX() < maxX && count < 6) {
                    count++;
                }
                break;
            //vertical
            case ZHONG:
                while (!enemyPoints.contains(point.setY(point.getY() - 1)) && point.getY() >= 0) {
                    count++;
                }
                point.setY(y);
                while (!enemyPoints.contains(point.setY(point.getY() + 1)) && point.getY() < maxY && count < 6) {
                    count++;
                }
                break;
            // diagonal
            case ZHENG_XIE:
                while (!enemyPoints.contains(point.setX(point.getX() - 1).setY(point.getY() + 1)) && point.getX() >= 0 && point.getY() < maxY) {
                    count++;
                }
                point.setX(x).setY(y);
                while (!enemyPoints.contains(point.setX(point.getX() + 1).setY(point.getY() - 1)) && point.getX() < maxX && point.getY() >= 0 && count < 6) {
                    count++;
                }
                break;
            // reverse diagonal
            case FAN_XIE:
                while (!enemyPoints.contains(point.setX(point.getX() - 1).setY(point.getY() - 1)) && point.getX() >= 0 && point.getY() >= 0) {
                    count++;
                }
                point.setX(x).setY(y);
                while (!enemyPoints.contains(point.setX(point.getX() + 1).setY(point.getY() + 1)) && point.getX() < maxX && point.getY() < maxY && count < 6) {
                    count++;
                }
                break;
        }
        return count;
    }

    @Override
    public void run(List<Point> humans, Point p) {
        allFreePoints.remove(humans.get(humans.size() - 1));

        Point result = null;
        try {
            result = doAnalysis(myPoints, humans);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            // may have bug when the board is almost full, AI will randomly place points
            Random random = new Random();
            int i = random.nextInt(allFreePoints.size());
            result = allFreePoints.get(i);
        }

        allFreePoints.remove(result);

        myPoints.add(result);
    }
}
