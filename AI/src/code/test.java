package code;

public class test {
	public static void tryRef(int []arr)
	{
		for(int i=0;i<arr.length;i++)
			arr[i]=5;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int [] arr = new int [] {1,2,3,4};
		tryRef(arr);
		for(int i=0;i<arr.length;i++)
			System.out.println(arr[i]);
		

	}

}
