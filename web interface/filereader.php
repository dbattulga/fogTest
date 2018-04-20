<?php
	function tail_file($filepath, $lines = 10) {
		$arr = trim(implode("", array_slice(file($filepath), -$lines)));
		//$fh = fopen($filepath, 'w' );
		//fclose($fh);
		return $arr;
	}

	$fp = tail_file("/home/text.txt");
	echo $fp;
?>
