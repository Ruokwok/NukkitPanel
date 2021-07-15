package cc.ruok.nukkitpanel.utils;

import cn.nukkit.math.NukkitMath;
import com.sun.management.OperatingSystemMXBean;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

public class Monitor {

    static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private static String cpu;

    static double getLoad() {
////        return osBean.getProcessCpuLoad();
//        try {
//            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
//            AttributeList list = null;
//            list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});
//            if (list.isEmpty()) return Double.NaN;
//            Attribute att = (Attribute) list.get(0);
//            Double value = (Double) att.getValue();
//            if (value == -1.0) return Double.NaN;
//            return ((int) (value * 1000) / 10.0);
//        } catch (Exception e) {
//            return -1.0D;
//        }
        double load = -1.0D;
        java.lang.management.OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        load = osMxBean.getSystemLoadAverage();
        com.sun.management.OperatingSystemMXBean osMxBean4;
        if (load == -1.0D) {
            osMxBean4 = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            load = osMxBean4.getSystemCpuLoad();
        }

        if (load == -1.0D) {
            osMxBean4 = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            load = osMxBean4.getProcessCpuLoad();
        }

        if (load == -1.0D) {
            osMxBean4 = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
            load = osMxBean4.getSystemLoadAverage();
        }

        return load;
    }

    public static double getOSMenRound() {
        double total = getOSTotalMen();
        double free = getOSFreeMen();
        double round = ((total - free) / total) * 100;
        BigDecimal b = new BigDecimal(round);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getOSMen() {
        long t = getOSTotalMen();
        long u = t - getOSFreeMen();
        return NukkitMath.round(u / 1073741824D, 2) + "GB / " + NukkitMath.round(t / 1073741824D, 2) + "GB";
    }

    static long getOSFreeMen() {
        return osBean.getFreePhysicalMemorySize();
    }

    static long getOSTotalMen() {
        return osBean.getTotalPhysicalMemorySize();
    }

    public static double getCPULoad() {
//        return osBean.getProcessCpuLoad();
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = null;
            list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});
            if (list.isEmpty()) return Double.NaN;
            Attribute att = (Attribute) list.get(0);
            Double value = (Double) att.getValue();
            if (value == -1.0) return Double.NaN;
            return ((int) (value * 1000) / 10.0);
        } catch (Exception e) {
            return -1.0D;
        }
    }

    public static String getRunningPath() {
        return System.getProperty("user.dir");
    }

    public static String getSystem() {
        return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")";
    }

    public static String getJre() {
        return System.getProperty("java.vendor") + " " + System.getProperty("java.version");
    }

    public static String getUser() {
        return System.getProperty("user.name");
    }

    public static String getCPUName() {
        if (cpu == null) {
            String system = getSystem();
            if (system.contains("Windows")) {
                cpu = Shell.exec("wmic cpu get name","gbk").replace("Name", "").trim();
            } else if (system.contains("Linux")) {
                cpu = Shell.execLinux("cat /proc/cpuinfo | grep name | cut -f2 -d:").trim();
            } else if (system.contains("Mac")) {
                cpu = Shell.exec("sysctl -n machdep.cpu.brand_string", "utf8").trim();
            } else {
                cpu = "Unknown";
            }
        }
        return cpu;
    }



}
