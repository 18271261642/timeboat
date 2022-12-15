
package com.imlaidian.ldclog;

public interface LdLogProtocolHandler {

    void log_flush();

    void log_write(String tag, String local_time, String lever, String type,  String thread_name,String log);

    void log_open(String file_name, String sub_path_dir);

    void log_init(String cache_path, String dir_path, String sub_dir_path, int max_file ,int outputType);

    void log_debug(boolean debug);

    void setOnLogProtocolStatus(OnLdLogProtocolStatus listener);
}
