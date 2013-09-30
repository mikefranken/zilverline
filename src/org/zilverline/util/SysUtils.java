/*
 * Copyright 2003-2004 Michael Franken, Zilverline.
 *
 * The contents of this file, or the files included with this file, are subject to
 * the current version of ZILVERLINE Collaborative Source License for the
 * Zilverline Search Engine (the "License"); You may not use this file except in
 * compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.zilverline.org.
 *
 * See the License for the rights, obligations and
 * limitations governing use of the contents of the file.
 *
 * The Original and Upgraded Code is the Zilverline Search Engine. The developer of
 * the Original and Upgraded Code is Michael Franken. Michael Franken owns the
 * copyrights in the portions it created. All Rights Reserved.
 *
 */

package org.zilverline.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The SystemErrors holds possible errorcodes for Various platforms.
 * 
 * @author Michael Franken
 * @version $Revision: 1.5 $
 * 
 * @since 18 september 2004
 */
public final class SysUtils {
    /** logger for Commons logging. */
    private static Log log = LogFactory.getLog(SysUtils.class);

    /**
     * Windows System Error Codes from http://msdn.microsoft.com/library/en-us/debug/base/system_error_codes__0-499_.asp
     */
    private static Map WINDOWS_SYSTEM_ERROR_CODES = new HashMap();

    private SysUtils() {
    }

    static {
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(0), "The operation completed successfully.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(1), "Incorrect function.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(2), "The system cannot find the file specified.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(3), "The system cannot find the path specified.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(4), "The system cannot open the file.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(5), "Access is denied.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(6), "The handle is invalid.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(7), "The storage control blocks were destroyed.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(8), "Not enough storage is available to process this command.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(9), "The storage control block address is invalid.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(10), "The environment is incorrect.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(11), "An attempt was made to load a program with an incorrect format.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(12), "The access code is invalid.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(13), "The data is invalid.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(14), "Not enough storage is available to complete this operation.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(15), "The system cannot find the drive specified.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(16), "The directory cannot be removed.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(17), "The system cannot move the file to a different disk drive.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(18), "There are no more files.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(19), "The media is write protected.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(20), "The system cannot find the device specified.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(21), "The device is not ready.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(22), "The device does not recognize the command.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(23), "Data error (cyclic redundancy check).");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(24), "The program issued a command but the command length is incorrect.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(25), "The drive cannot locate a specific area or track on the disk.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(26), "The specified disk or diskette cannot be accessed.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(27), "The drive cannot find the sector requested.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(28), "The printer is out of paper.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(29), "The system cannot write to the specified device.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(30), "The system cannot read from the specified device.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(31), "A device attached to the system is not functioning.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(32),
            "The process cannot access the file because it is being used by another process.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(33),
            "The process cannot access the file because another process has locked a portion of the file.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(34),
            "The wrong diskette is in the drive. Insert %2 (Volume Serial Number: %3) into drive %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(36), "Too many files opened for sharing.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(38), "Reached the end of the file.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(39), "The disk is full.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(50), "The request is not supported.");
        WINDOWS_SYSTEM_ERROR_CODES
            .put(
                new Integer(51),
                "Windows cannot find the network path. Verify that the network path is correct and the destination computer is not busy or turned off. If Windows still cannot find the network path, contact your network administrator.");
        WINDOWS_SYSTEM_ERROR_CODES
            .put(
                new Integer(52),
                "You were not connected because a duplicate name exists on the network. Go to System in the Control Panel to change the computer name and try again.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(53), "The network path was not found.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(54), "The network is busy.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(55), "The specified network resource or device is no longer available.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(56), "The network BIOS command limit has been reached.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(57), "A network adapter hardware error occurred.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(58), "The specified server cannot perform the requested operation.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(59), "An unexpected network error occurred.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(60), "The remote adapter is not compatible.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(61), "The printer queue is full.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(62),
            "Space to store the file waiting to be printed is not available on the server.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(63), "Your file waiting to be printed was deleted.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(64), "The specified network name is no longer available.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(65), "Network access is denied.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(66), "The network resource type is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(67), "The network name cannot be found.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(68), "The name limit for the local computer network adapter card was exceeded.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(69), "The network BIOS session limit was exceeded.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(70), "The remote server has been paused or is in the process of being started.");
        WINDOWS_SYSTEM_ERROR_CODES
            .put(
                new Integer(71),
                "No more connections can be made to this remote computer at this time because there are already as many connections as the computer can accept.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(72), "The specified printer or disk device has been paused.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(80), "The file exists.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(82), "The directory or file cannot be created.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(83), "Fail on INT 24.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(84), "Storage to process this request is not available.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(85), "The local device name is already in use.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(86), "The specified network password is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(87), "The parameter is incorrect.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(88), "A write fault occurred on the network.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(89), "The system cannot start another process at this time.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(100), "Cannot create another system semaphore.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(101), "The exclusive semaphore is owned by another process.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(102), "The semaphore is set and cannot be closed.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(103), "The semaphore cannot be set again.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(104), "Cannot request exclusive semaphores at interrupt time.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(105), "The previous ownership of this semaphore has ended.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(106), "Insert the diskette for drive %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(107), "The program stopped because an alternate diskette was not inserted.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(108), "The disk is in use or locked by another process.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(109), "The pipe has been ended.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(110), "The system cannot open the device or file specified.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(111), "The file name is too long.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(112), "There is not enough space on the disk.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(113), "No more internal file identifiers available.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(114), "The target internal file identifier is incorrect.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(117), "The IOCTL call made by the application program is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(118), "The verify-on-write switch parameter value is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(119), "The system does not support the command requested.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(120), "This function is not supported on this system.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(121), "The semaphore timeout period has expired.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(122), "The data area passed to a system call is too small.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(123), "The filename, directory name, or volume label syntax is incorrect.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(124), "The system call level is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(125), "The disk has no volume label.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(126), "The specified module could not be found.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(127), "The specified procedure could not be found.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(128), "There are no child processes to wait for.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(129), "The %1 application cannot be run in Win32 mode.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(130),
            "Attempt to use a file handle to an open disk partition for an operation other than raw disk I/O.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(131),
            "An attempt was made to move the file pointer before the beginning of the file.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(132), "The file pointer cannot be set on the specified device or file.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(133),
            "A JOIN or SUBST command cannot be used for a drive that contains previously joined drives.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(134),
            "An attempt was made to use a JOIN or SUBST command on a drive that has already been joined.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(135),
            "An attempt was made to use a JOIN or SUBST command on a drive that has already been substituted.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(136), "The system tried to delete the JOIN of a drive that is not joined.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(137),
            "The system tried to delete the substitution of a drive that is not substituted.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(138), "The system tried to join a drive to a directory on a joined drive.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(139),
            "The system tried to substitute a drive to a directory on a substituted drive.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(140), "The system tried to join a drive to a directory on a substituted drive.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(141), "The system tried to SUBST a drive to a directory on a joined drive.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(142), "The system cannot perform a JOIN or SUBST at this time.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(143),
            "The system cannot join or substitute a drive to or for a directory on the same drive.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(144), "The directory is not a subdirectory of the root directory.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(145), "The directory is not empty.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(146), "The path specified is being used in a substitute.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(147), "Not enough resources are available to process this command.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(148), "The path specified cannot be used at this time.");
        WINDOWS_SYSTEM_ERROR_CODES
            .put(new Integer(149),
                "An attempt was made to join or substitute a drive for which a directory on the drive is the target of a previous substitute.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(150),
            "System trace information was not specified in your CONFIG.SYS file, or tracing is disallowed.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(151),
            "The number of specified semaphore events for DosMuxSemWait is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(152), "DosMuxSemWait did not execute");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(153), "The DosMuxSemWait list is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(154),
            "The volume label you entered exceeds the label character limit of the target file system.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(155), "Cannot create another thread.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(156), "The recipient process has refused the signal.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(157), "The segment is already discarded and cannot be locked.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(158), "The segment is already unlocked.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(159), "The address for the thread ID is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(160), "The argument string passed to DosExecPgm is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(161), "The specified path is invalid.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(162), "A signal is already pending.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(164), "No more threads can be created in the system.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(167), "Unable to lock a region of a file.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(170), "The requested resource is in use.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(173), "A lock request was not outstanding for the supplied cancel region.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(174), "The file system does not support atomic changes to the lock type.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(180), "The system detected a segment number that was not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(182), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(183), "Cannot create a file when that file already exists.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(186), "The flag passed is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(187), "The specified system semaphore name was not found.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(188), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(189), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(190), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(191), "Cannot run %1 in Win32 mode.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(192), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(193), "%1 is not a valid Win32 application.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(194), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(195), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(196), "The operating system cannot run this application program.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(197),
            "The operating system is not presently configured to run this application.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(198), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(199), "The operating system cannot run this application program.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(200), "The code segment cannot be greater than or equal to 64K.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(201), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(202), "The operating system cannot run %1.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(203), "The system could not find the environment option that was entered.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(205), "No process in the command subtree has a signal handler.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(206), "The filename or extension is too long.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(207), "The ring 2 stack is in use.");
        WINDOWS_SYSTEM_ERROR_CODES
            .put(new Integer(208),
                "The global filename characters, * or ?, are entered incorrectly or too many global filename characters are specified.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(209), "The signal being posted is not correct.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(210), "The signal handler cannot be set.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(212), "The segment is locked and cannot be reallocated.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(214),
            "Too many dynamic-link modules are attached to this program or dynamic-link module.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(215), "Cannot nest calls to LoadModule.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(216),
            "The image file %1 is valid, but is for a machine type other than the current machine.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(217), "The image file %1 is signed, unable to modify.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(218), "The image file %1 is strong signed, unable to modify.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(230), "The pipe state is invalid.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(231), "All pipe instances are busy.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(232), "The pipe is being closed.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(233), "No process is on the other end of the pipe.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(234), "More data is available.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(240), "The session was canceled.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(254), "The specified extended attribute name was invalid.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(255), "The extended attributes are inconsistent.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(258), "The wait operation timed out.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(259), "No more data is available.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(266), "The copy functions cannot be used.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(267), "The directory name is invalid.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(275), "The extended attributes did not fit in the buffer.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(276), "The extended attribute file on the mounted file system is corrupt.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(277), "The extended attribute table file is full.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(278), "The specified extended attribute handle is invalid.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(282), "The mounted file system does not support extended attributes.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(288), "Attempt to release mutex not owned by caller.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(298), "Too many posts were made to a semaphore.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(299),
            "Only part of a ReadProcessMemory or WriteProcessMemory request was completed.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(300), "The oplock request is denied.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(301), "An invalid oplock acknowledgment was received by the system.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(302), "The volume is too fragmented to complete this operation.");
        WINDOWS_SYSTEM_ERROR_CODES
            .put(new Integer(303), "The file cannot be opened because it is in the process of being deleted.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(317),
            "The system cannot find message text for message number 0x%1 in the message file for %2.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(318), "The scope specified was not found.");
        WINDOWS_SYSTEM_ERROR_CODES.put(new Integer(487), "Attempt to access invalid address.");
    }

    public static String getErrorTextById(final int id) {
        String os = System.getProperty("os.name");

        if (os.indexOf("Windows") >= 0) {
            return (String) WINDOWS_SYSTEM_ERROR_CODES.get(new Integer(id));
        }

        return "";
    }

    /**
     * Picks a random number between 0 and (below) the givenNumber.
     * 
     * @param number the givenNumber
     * 
     * @return a number n: 0 &lt;= n &lt; givenNumber
     */
    public static boolean canExecute(final String cmd) {
        Process proc = null;
        log.debug("Can we Execute cmd: " + cmd);
        try {
            proc = Runtime.getRuntime().exec(cmd);
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            proc.waitFor();
            log.debug("Exit value: " + proc.exitValue());
            log.error("Exit value: " + SysUtils.getErrorTextById(proc.exitValue()));
            return proc.exitValue() == 0;
        }
        catch (Exception e) {
            log.error("Can't execute: " + cmd, e);

            if (proc != null) {
                log.error(" --> Can't execute: '" + cmd + "'. Exit value: " + proc.exitValue());
                log.error(" --> Can't execute: '" + cmd + "'. " + SysUtils.getErrorTextById(proc.exitValue()));
            }
        }
        return false;
    }

    /**
     * Execute a system program on a file in a certain directory.
     * 
     * @param unArchiveCommand the command to run, including possible options
     * @param sourceFile the file
     * @param unPackDestinationDirectory the directory
     * @return true on succes, false otherwise
     */
    public static boolean execute(final String unArchiveCommand, File sourceFile, File unPackDestinationDirectory) {
        Process proc = null;
        String[] cmd = null;
        StringBuffer cmd4Log = new StringBuffer();
        log.debug("unArchiveCommand: " + unArchiveCommand);
        // prepare the command by first creating a command array, containing the program, options and source file
        // first parse the command from config
        String[] tmpCmd = unArchiveCommand.split(" ");

        try {
            // and then copy the thing into a new larger array
            cmd = new String[tmpCmd.length + 1];
            for (int i = 0; i < tmpCmd.length; i++) {
                cmd[i] = tmpCmd[i];
                // for logging, never mind the overhead
                cmd4Log.append(cmd[i] + " ");
            }
            // cmd[tmpCmd.length] = "\"" + sourceFile.getAbsolutePath() + "\"";
            cmd[tmpCmd.length] = sourceFile.getAbsolutePath();
            cmd4Log.append(cmd[tmpCmd.length]);
            log.debug("Executing: '" + cmd4Log + "' from working directory '" + unPackDestinationDirectory + "'");
            // run the command in the target directory (unPackDestinationDirectory) on the archive (sourceFile)
            // TODO: I pass cmd4Log instead of cmd[], since hh.exe does not do well...
            proc = Runtime.getRuntime().exec(cmd4Log.toString(), null, unPackDestinationDirectory);
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            proc.waitFor();
            log.debug("Exit value: " + SysUtils.getErrorTextById(proc.exitValue()));
            // everthing OK?
            if (proc.exitValue() == 0) {
                // got it right first time
                log.debug("Succesfully executed: '" + cmd4Log + "'.");
                return true;
            } else {
                // error executing proc
                log.debug("Retrying, couldn't execute: '" + cmd4Log + "'. Exit value: "
                    + SysUtils.getErrorTextById(proc.exitValue()));

                // try again with cmd
                proc = Runtime.getRuntime().exec(cmd, null, unPackDestinationDirectory);
                // any error message?
                errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
                // any output?
                outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
                // kick them off
                errorGobbler.start();
                outputGobbler.start();
                proc.waitFor();
                log.debug("Exit value: " + SysUtils.getErrorTextById(proc.exitValue()));

                if (proc.exitValue() != 0) {
                    // error executing proc
                    log.warn(" --> Can't execute in retry (with cmd[]): '" + cmd4Log + "'. Exit value: " + proc.exitValue());
                    log.warn(" --> Can't execute: '" + cmd4Log + "'. " + SysUtils.getErrorTextById(proc.exitValue()));
                } else {
                    log.debug("Succesfully executed in retry: '" + cmd4Log + "'.");
                    return true;
                }
            }
        }
        catch (Exception e) {
            log.error("Can't execute: " + cmd4Log, e);

            if (proc != null) {
                log.error(" --> Can't execute: '" + cmd4Log + "'. Exit value: " + proc.exitValue());
                log.error(" --> Can't execute: '" + cmd4Log + "'. " + SysUtils.getErrorTextById(proc.exitValue()));
            }
        }
        return false;
    }
}
