set terminal png;
set output "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.dctcp_report_users_range.csv_graphs/averageWaitingTime.png";

set style line 1 lt 1 lw 1 linecolor rgb "red";
set style line 2 lt 1 lw 1 linecolor rgb "blue";

set xlabel "nusr";
set ylabel "averageWaitingTime";
set ytics nomirror
set grid;

plot "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.dctcp_report_users_range.csv_graphs/averageWaitingTime.dat" using 1:2 title "averageWaitingTime" with linespoints axes x1y1

