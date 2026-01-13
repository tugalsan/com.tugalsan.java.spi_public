package com.tugalsan.java.spi.helloworld;

import module com.tugalsan.java.core.function;
import module com.tugalsan.java.core.log;
import module com.tugalsan.java.core.network;
import module com.tugalsan.java.core.sql.basic;
import module com.tugalsan.java.core.sql.conn;
import module com.tugalsan.java.core.sql.select;
import module com.tugalsan.java.core.string;
import module com.tugalsan.java.core.tomcat;
import module javax.servlet.api;
import java.nio.file.*;

@WebListener
public class AppServlet implements ServletContextListener {

    final private static TS_Log d = TS_Log.of(AppServlet.class);

    public static String APP_NAME;

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        APP_NAME = TS_TomcatPathUtils.getWarNameLabel(evt);
        TS_LogUtils.MAP = txt -> TGS_StringUtils.cmn().concat("[", APP_NAME, "] ", txt);
        d.ci("contextInitialized", "coloring console...");
        TS_LogUtils.setColoredConsole(true);
        d.ci("contextInitialized", "disableing ssl validation...");
        TS_NetworkSSLUtils.disableCertificateValidation();
        d.ci("contextInitialized", "reading db config...");
        var u_dbConfig = TS_SQLConnAnchor.of(Path.of("C:\\dat\\sql\\cnn"), "autosqlweb");
        if (u_dbConfig.isExcuse()) {
            d.ce("contextInitialized", u_dbConfig.excuse().getMessage());
            return;
        }
        var anchor = u_dbConfig.value();
        d.ci("contextInitialized", "anchor", anchor);

        d.ci("contextInitialized", "reading table values...");
        TS_SQLSelectUtils.select(anchor, "aktif").columnsAll()
                .whereConditionNone().groupNone().orderNone()
                .rowIdxOffsetNone().rowSizeLimitNone()
                .walkRows(TGS_FuncMTU_In1.empty, (rs, ri) -> {
                    d.cr("contextInitialized", "aktif", "walkRows", ri, rs.lng.get(0), rs.str.get(1));
                });

        var basicConfigCommon = new TGS_SQLBasicConfig("common", "LNG_ID", "STR254_ADI", "BYTESSTR_VALUE");
        d.ci("contextInitialized", "basicConfigCommon", basicConfigCommon);

        TS_SQLBasicUtils.createCommonTableIfNotExists(anchor, basicConfigCommon);
        d.ci("contextInitialized", "done...");
    }

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        d.ci("contextDestroyed", "destroying TS_LogUtils...");
        TS_LogUtils.destroy();
        d.ci("contextDestroyed", "CONTEXT DESTROYED");
    }
}
