package org.refined.gwdbosser.misc;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.*;
import java.util.logging.Formatter;

/**
 * @author Float [powerbot.org]
 * 
 * This source code giving you errors? It's because you are not running Java 7.
 * RSBot will only load scripts compiled with Java 7 so this may be your issue
 */
public class ScriptInjector {

    private static final String RSBOT_DICTIONARY = "C:\\Users\\Ben\\Dropbox\\Powerbot\\GWDBosser\\RSBot.jar"; // = "C:\\Users\\Public\\Documents\\RSBot\\RSBot.jar"; - EXAMPLE
    private static final boolean DEV_MODE = true; //Change to false if you want RSBot to run with SDN scripts (not dev mode)

    private static final Logger logger = Logger.getLogger(ScriptInjector.class.getName());
    private static URLClassLoader RSBot;

    public static void main(final String[] args) {
        final File rsbot = getRSBot();
        logger.setUseParentHandlers(false);

        final ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Format());

        logger.addHandler(handler);
        if(rsbot != null && rsbot.exists()) {
            logger.info("Located RSBot, retrieving class files.");
            try {
                RSBot = URLClassLoader.newInstance(new URL[]{rsbot.toURI().toURL()});
            } catch(final MalformedURLException e) {
                e.printStackTrace();
            }
            if(RSBot == null) {
                logger.severe("Unable to load RSBot, stopping.");
            } else {
                final String javaVersion = System.getProperty("java.version");
                if(javaVersion != null && !javaVersion.startsWith("1.7")) {
                    logger.severe("You don't have or are not running Java 7. You should get it at the java website");
                    logger.severe("Since that may be the reason local scripts don't work for you (Make sure scripts are compiled with Java 7 too)");
                }
                if(getRSBotClasses(rsbot)) {
                    logger.info("Loaded all of the necessary RSBot classes.");
                    logger.info("Loading RSBot, Developer Mode: " + DEV_MODE);
                    try {
                        final Class<?> mainClazz = RSBot.loadClass("org.powerbot.Boot");
                        if(mainClazz != null) {
                            final Method mainMethod = mainClazz.getDeclaredMethod("main", String[].class);
                            if(mainMethod != null) {
                                mainMethod.setAccessible(true);
                                mainMethod.invoke(null, (Object) new String[]{DEV_MODE ? "-dev" : ""});
                            }
                        }
                    } catch(final ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace(System.err);
                        logger.severe("Unable to start RSBot, stopping.");
                        return;
                    }
                    logger.info("RSBot successfully loaded. ScriptInjector has officially started.");
                    new Thread(new SelectorListener(), "ScriptInjector").start();
                } else {
                    logger.severe("Unable to load all of RSBots needed class files, stopping.");
                }
            }
        } else {
            logger.severe("Unable to locate RSBot.jar, please specify a dictionary in the ScriptInjector class.");
        }
    }

    private static File getRSBot() {
        if(RSBOT_DICTIONARY != null)
            return new File(RSBOT_DICTIONARY);
        final List<File> possible = getContent(new File("./"));
        if(possible != null && possible.size() > 0) {
            for(final File file : possible)
                if(file != null && file.exists() && file.isFile() && file
                        .getName().toLowerCase().contains("rsbot")) {
                    final String name = file.getName().toLowerCase();
                    if(name.contains("rsbot") && name.endsWith(".jar"))
                        return file;
                }
        }
        return null;
    }

    private static List<File> getContent(final File dict) {
        final List<File> list = new ArrayList<>();
        if(dict != null && dict.exists()) {
            final File[] files = dict.listFiles();
            if(files != null && files.length > 0)
                for(final File file : files) {
                    if(file.isDirectory())
                        list.addAll(getContent(file));
                    else {
                        list.add(file);
                    }
                }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private static boolean getRSBotClasses(final File rsbot) {
        try {
            final JarInputStream jar = new JarInputStream(new FileInputStream(rsbot));
            while(true) {
                try {
                    final JarEntry entry = jar.getNextJarEntry();
                    if(entry == null)
                        break;
                    else if(entry.getName().endsWith(".class")) {
                        final Class<?> clazz = RSBot.loadClass(entry.getName().replaceAll("/", "\\.").replaceAll("\\.class", ""));
                        if(clazz != null) {
                            if(clazz.getName().endsWith(".Bot"))
                                logger.info("[Class] Bot[" + (Bot = clazz).getName() + "] class loaded.");
                            if(clazz.getName().endsWith(".Script"))
                                logger.info("[Class] Script[" + (Script = clazz).getName() + "] class loaded.");
                            if(clazz.getName().endsWith(".Manifest"))
                                logger.info("[Class] Manifest[" + (Manifest = (Class<? extends Annotation>) clazz).getName() + "] class loaded.");
                            if(clazz.getName().endsWith(".ScriptHandler"))
                                logger.info("[Class] ScriptHandler[" + (ScriptHandler = clazz).getName() + "] class loaded.");
                            if(ArrayList.class.isAssignableFrom(clazz))
                                logger.info("[Class] AccList[" + (AccList = clazz).getName() + "] class loaded.");
                            if(JFrame.class.isAssignableFrom(clazz) && WindowListener.class.isAssignableFrom(clazz))
                                logger.info("[Class] Canvas[" + (Canvas = clazz).getName() + "] class loaded.");
                            if(JToolBar.class.isAssignableFrom(clazz))
                                logger.info("[Class] Toolbar[" + (Toolbar = clazz).getName() + "] class loaded.");
                            if(JDialog.class.isAssignableFrom(clazz) && ActionListener.class
                                    .isAssignableFrom(clazz) && Reflect.getLogger(clazz) != null)
                                logger.info("[Class] ScriptSelector[" + (ScriptSelector = clazz).getName() + "] class loaded.");
                            if(Comparable.class.isAssignableFrom(clazz) && Serializable.class.isAssignableFrom(clazz)
                                    && clazz.getModifiers() == (Modifier.PUBLIC | Modifier.FINAL))
                                logger.info("[Class] ScriptDef[" + (ScriptDef = clazz).getName() + "] class loaded.");
                        }
                    }
                } catch(final IOException | ClassNotFoundException e) {
                    e.printStackTrace(System.err);
                }
            }
            if(ScriptSelector != null)
                for(final Class<?> clazz : ScriptSelector.getDeclaredClasses()) {
                    if(clazz != null && JPanel.class.isAssignableFrom(clazz))
                        logger.info("[Class] ScriptPanel[" + (ScriptPanel = clazz).getName() + "] class loaded.");
                }
            if(AccList != null)
                for(final Class<?> clazz : AccList.getDeclaredClasses()) {
                    if(clazz != null)
                        logger.info("[Class] AccInfo[" + (AccInfo = clazz).getName() + "] class loaded.");
                }
            return ScriptSelector != null && Manifest != null && Toolbar != null && Canvas != null && Script != null && AccInfo != null
                    && ScriptDef != null && Bot != null && AccList != null && ScriptHandler != null && ScriptPanel != null;
        } catch(final IOException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    private static class Reflect {

        private static Constructor getConstructor(final Class<?> clazz, final Class<?>... params) {
            try {
                return clazz.getConstructor(params);
            } catch(final NoSuchMethodException e) {
                e.printStackTrace(System.err);
            }
            return null;
        }

        private static Method getMethod(final Class<?> clazz, final Class<?> returnType, final Class<?>... params) {
            for(final Method method : clazz.getDeclaredMethods()) {
                if(method.getReturnType().equals(returnType) && Arrays.equals(method.getParameterTypes(), params)) {
                    method.setAccessible(true);
                    return method;
                }
            }
            return null;
        }

        private static Logger getLogger(final Class<?> clazz) {
            for(final Field field : clazz.getDeclaredFields())
                if(field != null && (field.getModifiers() & Modifier.STATIC) != 0) {
                    field.setAccessible(true);
                    final Object obj;
                    try {
                        obj = field.get(null);
                        if(obj != null && obj instanceof Logger)
                            return (Logger) obj;
                    } catch(final IllegalAccessException e) {
                        e.printStackTrace(System.err);
                    }
                }
            return null;
        }
    }

    private static class SelectorListener extends Thread {

        private boolean added = false;

        @Override
        public void run() {
            while(true) {
                JDialog scriptSelector = null;
                try {
                    scriptSelector = getScriptSelector();
                } catch(final IllegalAccessException | InstantiationException e) {
                    e.printStackTrace(System.err);
                }
                if(scriptSelector == null && added) {
                    added = false;
                } else if(scriptSelector != null && !added) {
                    logger.info("ScriptSelector detected, manually adding scripts to display.");

                    final Class<?>[] scripts = getScripts();
                    if(scripts != null && scripts.length > 0) {
                        final Constructor constructor = Reflect.getConstructor(ScriptDef, Manifest);
                        if(constructor != null) {
                            final Map<Class<?>, Object> scriptDef = new HashMap<>(scripts.length);
                            constructor.setAccessible(true);
                            for(final Class<?> clazz : scripts)
                                if(clazz.isAnnotationPresent(Manifest) && Script.isAssignableFrom(clazz)) {
                                    try {
                                        scriptDef.put(clazz, constructor.newInstance(clazz.getAnnotation(Manifest)));
                                    } catch(final InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace(System.err);
                                    }
                                }
                            if(scriptDef.size() > 0) {
                                JPanel panel = null;
                                for(final Field field : scriptSelector.getClass().getDeclaredFields())
                                    if(field.getType() != null && field.getType().equals(JPanel.class)) {
                                        field.setAccessible(true);
                                        try {
                                            panel = (JPanel) field.get(scriptSelector);
                                        } catch(final IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                if(panel != null) {
                                    final Constructor sectionConst = Reflect.getConstructor(ScriptPanel, ScriptSelector, Component.class, ScriptDef);
                                    if(sectionConst != null) {
                                        sectionConst.setAccessible(true);
                                        for(final Class<?> clazz : scriptDef.keySet()) {
                                            final Object obj = scriptDef.get(clazz);
                                            try {
                                                final JPanel section = (JPanel) sectionConst.newInstance(scriptSelector, panel, obj);
                                                if(section != null) {
                                                    final JButton button = getJButton(section);
                                                    if(button != null) {
                                                        for(final ActionListener al : button.getActionListeners())
                                                            button.removeActionListener(al);
                                                        button.addActionListener(new ScriptRunner(clazz, scriptSelector, obj, section));
                                                    }
                                                    panel.add(section);
                                                }
                                            } catch(final InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                                e.printStackTrace(System.err);
                                            }
                                        }
                                        panel.validate();
                                        panel.repaint();
                                    }
                                } else logger.severe("Unable to find JPanel to place scripts on!");
                            }
                        }
                    } else logger.severe("No scripts available, or error reading scripts.");
                    added = true;
                }
                try {
                    Thread.sleep(1000l);
                } catch(final InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        private JButton getJButton(final JPanel panel) {
            for(final Component comp : panel.getComponents())
                if(comp != null) {
                    if(comp instanceof JPanel) {
                        final JButton button = getJButton((JPanel) comp);
                        if(button != null)
                            return button;
                    } else if(comp instanceof JButton)
                        return (JButton) comp;
                }
            return null;
        }

        private JDialog getScriptSelector() throws IllegalAccessException, InstantiationException {
            for(final Window window : Window.getWindows())
                if(window != null && window.isVisible() && window instanceof JDialog) {
                    final JDialog dialog = (JDialog) window;
                    if(dialog.getTitle() != null && dialog.getTitle().startsWith("Scripts"))
                        return dialog;
                }
            return null;
        }

        private static Class<?> findScript(final File file, final URLClassLoader loader) {
            final List<File> content = file.isDirectory() ? getContent(file) : Arrays.asList(file);
            if(content != null && content.size() > 0) {
                for(final File inner : content)
                    if(inner != null && inner.exists() && inner.getName().toLowerCase().endsWith(".class")) {
                        try {
                            final Class<?> clazz = loader.loadClass(inner.getCanonicalPath().replace(file.getCanonicalPath()
                                    .replace(file.getName(), ""), "").replaceAll("\\\\", "\\.").replace(".class", ""));
                            if(clazz != null && Script.isAssignableFrom(clazz))
                                return clazz;
                        } catch(final ClassNotFoundException | IOException e) {
                            e.printStackTrace(System.err);
                        }
                    }
            }
            return null;
        }

        private static Class<?>[] getScripts() {
            final File bin = new File("./bin"), out = new File("./out");
            final File[] content = (bin.exists() ? bin : out).listFiles();
            final List<Class<?>> scripts = new ArrayList<>();
            if(content != null && content.length > 0) {
                for(final File file : content) {
                    URLClassLoader loader = null;
                    try {
                        loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
                    } catch(final MalformedURLException e) {
                        e.printStackTrace(System.err);
                    }
                    if(loader == null)
                        continue;
                    final Class<?> script = findScript(file, loader);
                    if(script != null)
                        scripts.add(script);
                }
                return scripts.toArray(new Class<?>[scripts.size()]);
            }
            return null;
        }

    }

    private static class ScriptRunner implements ActionListener {

        private final Object scriptDef;
        private final Class<?> scriptClazz;
        private final JDialog scriptSelector;
        private final JPanel section;

        private ScriptRunner(final Class<?> scriptClazz, final JDialog scriptSelector, final Object scriptDef, final JPanel section) {
            this.section = section;
            this.scriptDef = scriptDef;
            this.scriptClazz = scriptClazz;
            this.scriptSelector = scriptSelector;

            if(scriptClazz == null || scriptSelector == null)
                logger.severe("Error setting up a script panel.");
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            final Object manifest = scriptClazz.getAnnotation(Manifest);
            if(manifest != null) {
                try {
                    final Method method = manifest.getClass().getMethod("name");
                    method.setAccessible(true);
                    logger.info("Loading injected script: " + method.invoke(manifest));

                    final Object script = scriptClazz.asSubclass(Script).newInstance();
                    if(script != null) {
                        section.setVisible(false);
                        scriptSelector.dispose();
                        final Method getInstance = Reflect.getMethod(Bot, Bot);
                        final Object bot = getInstance.invoke(null);
                        if(bot != null) {
                            //account
                            final Method setAccount = Reflect.getMethod(bot.getClass(), Void.TYPE, AccInfo);
                            setAccount.invoke(bot, (Object) null);
                            String currentAcc = null;
                            for(final Field field : scriptSelector.getClass().getDeclaredFields())
                                if(field != null && field.getType().equals(JButton.class)){
                                    field.setAccessible(true);
                                    final JButton button = (JButton) field.get(scriptSelector);
                                    if(button.getText() != null && button.getText().trim().length() > 0)
                                        currentAcc = button.getText();
                                }
                            final Method getAccList = Reflect.getMethod(AccList, AccList);
                            for(final Object acc : (List<?>) getAccList.invoke(null)){
                                final Method toString = acc.getClass().getDeclaredMethod("toString");
                                if(!((String) toString.invoke(acc)).equalsIgnoreCase(currentAcc))
                                    continue;
                                setAccount.invoke(bot, acc);
                                break;
                            }
                            //script
                            final Method getHandler = Reflect.getMethod(bot.getClass(), ScriptHandler);
                            final Object handler = getHandler.invoke(bot);
                            final Method startMethod = Reflect.getMethod(handler.getClass(), Boolean.TYPE, Script, ScriptDef);
                            startMethod.invoke(handler, script, scriptDef);
                            logger.info("Script successfully loaded and executed with " + currentAcc);
                        } else logger.severe("No bot running. Script execution failed.");
                    } else logger.severe("Unable to instantiate script.");
                } catch(final NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
                    e1.printStackTrace(System.err);
                }
            } else logger.severe("Error getting script manifest.");
        }

    }

    private static class Format extends Formatter {
        private static final DateFormat df = new SimpleDateFormat("hh:mm:ss");

        public String format(final LogRecord record) {
            final StringBuilder builder = new StringBuilder();
            final String[] source = record.getSourceClassName().split("\\$");
            builder.append("[").append(df.format(new Date(record.getMillis()))).append("]");
            builder.append("[").append(source[source.length - 1]).append(".");
            builder.append(record.getSourceMethodName()).append("] ");
            builder.append(super.formatMessage(record)).append("\n");
            return builder.toString();
        }

        public final String getHead(final Handler h) {
            return super.getHead(h);
        }

        public final String getTail(final Handler h) {
            return super.getTail(h);
        }
    }

    private static Class<?> ScriptSelector, Toolbar, Canvas, Script, ScriptDef, Bot, AccList, ScriptHandler, ScriptPanel, AccInfo;
    private static Class<? extends Annotation> Manifest;

}