set terminal png;
set output "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.dctcp_report_users_range.csv_graphs/averageQueueLength.png";

set style line 1 lt 1 lw 1 linecolor rgb "red";
set style line 2 lt 1 lw 1 linecolor rgb "#778899";

set xlabel "nusr";
set ylabel "averageQueueLength";
set ytics nomirror
set grid;

plot "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.dctcp_report_users_range.csv_graphs/averageQueueLength.dat" using 1:2 title "averageQueueLength" with linespoints axes x1y1, "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.dctcp_report_users_range.csv_graphs/averageQueueLength.dat" notitle with yerrorbars ls 2;

