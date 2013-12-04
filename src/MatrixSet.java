/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 04.12.13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class MatrixSet {
    public int[][] trMatrix;
    public int[][] valMatrix;

    public MatrixSet(int[][] trainingMatrix, int[][] validationMatrix) {
        trMatrix = trainingMatrix;
        valMatrix = validationMatrix;
    }
}
