set terminal png;
set output "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.tcp_report_users_range.csv_graphs/averageWindowSize.png";

set style line 1 lt 1 lw 1 linecolor rgb "red";
set style line 2 lt 1 lw 1 linecolor rgb "#778899";

set xlabel "nusr";
set ylabel "averageWindowSize";
set ytics nomirror
set grid;

plot "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.tcp_report_users_range.csv_graphs/averageWindowSize.dat" using 1:2 title "averageWindowSize" with linespoints axes x1y1, "/home/bhavin/MTech/Courses/CS 681 Performance Analysis/project/681 Simulation/bin/output.tcp_report_users_range.csv_graphs/averageWindowSize.dat" notitle with yerrorbars ls 2;

