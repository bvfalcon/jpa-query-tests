package name.bychkov.jee;

public class Random extends java.util.Random
{
	private static final long serialVersionUID = 7374884472390968098L;
	
	int[][] intervals = new int[][] { { 48, 57 }, { 65, 90 }, { 97, 122 } };
	int intervalsCommonLength = 0;
	{
		for (int[] interval : intervals)
		{
			intervalsCommonLength += interval[1] - interval[0] + 1;
		}
	}
	
	public String nextString(int length)
	{
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length)
		{
			int rndInt = this.nextInt(intervalsCommonLength);
			for (int i = 0; i < intervals.length; i++)
			{
				int[] interval = intervals[i];
				int intervalLength = interval[1] - interval[0] + 1;
				if (rndInt < intervalLength)
				{
					sb.append((char) (interval[0] + rndInt));
					break;
				}
				else
				{
					rndInt -= intervalLength;
				}
			}
		}
		return sb.toString();
	}
	
	/* check correctness */
	public static void main(String... strings)
	{
		Random random = new Random();
		for (int i = 0; i < 100; i++)
		{
			System.out.println(random.nextString(10));
		}
	}
}