set terminal png;
set output "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.tcp_report_users_range.csv_graphs/collectiveThroughput.png";

set style line 1 lt 1 lw 1 linecolor rgb "red";
set style line 2 lt 1 lw 1 linecolor rgb "blue";

set xlabel "nusr";
set ylabel "collectiveThroughput";
set ytics nomirror
set grid;

plot "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.tcp_report_users_range.csv_graphs/collectiveThroughput.dat" using 1:2 title "collectiveThroughput" with linespoints axes x1y1
