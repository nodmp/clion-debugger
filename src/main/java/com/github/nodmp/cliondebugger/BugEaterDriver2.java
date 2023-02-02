package com.github.nodmp.cliondebugger;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.BaseProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.jetbrains.cidr.execution.Installer;
import com.jetbrains.cidr.execution.debugger.backend.*;
import com.jetbrains.cidr.execution.debugger.memory.Address;
import com.jetbrains.cidr.execution.debugger.memory.AddressRange;
import com.jetbrains.cidr.system.HostMachine;
import com.jetbrains.cidr.system.LocalHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BugEaterDriver2 extends DebuggerDriver {

    private final AtomicInteger lastId = new AtomicInteger(1);
    private final List<LLThread> fakeThreads = Collections.singletonList(new LLThread(1, "stop", "main-UI", "Android Main", "12"));
    private String fakeSrcFileName = "main.cpp";
    private int fakeSrcFileLine = 4;
    private final BaseProcessHandler<?> myProcessHandler;

    public BugEaterDriver2(@NotNull Handler handler) {
        super(handler);
        myProcessHandler = new FakeProcessHandler();
    }

    @Override
    public boolean supportsWatchpointLifetime() {
        return false;
    }

    @Override
    public @NotNull Language getConsoleLanguage() {
        return BugEaterLanguage.INSTANCE;
    }

    @Override
    public @NotNull BaseProcessHandler getProcessHandler() {
        return myProcessHandler;
    }


    @Override
    public boolean isInPromptMode() {
        return false;
    }

    @Override
    public @NotNull HostMachine getHostMachine() {
        return LocalHost.INSTANCE;
    }

    @Override
    public void setValuesFilteringEnabled(boolean b) throws ExecutionException {

    }

    @Override
    public @NotNull Inferior loadForLaunch(@NotNull Installer installer, @Nullable String s) throws ExecutionException {
        Project project = ProjectUtil.guessProjectForFile(VfsUtil.findFileByIoFile(installer.getExecutableFile(), true));
        return new FakeInferior(project);
    }

    @Override
    public @NotNull Inferior loadForAttach(int i) throws ExecutionException {
        throw new ExecutionException("Not supported");
    }

    @Override
    public @NotNull Inferior loadForAttach(@NotNull String s, boolean b) throws ExecutionException {
        throw new ExecutionException("Attaching by name is not supported");
    }

    @Override
    public @NotNull Inferior loadCoreDump(@NotNull File file, @Nullable File file1, @Nullable File file2, @NotNull List<PathMapping> list) throws ExecutionException {
        throw new ExecutionException("Not support core dump");
    }

    @Override
    public @NotNull Inferior loadCoreDump(@NotNull File file, @Nullable File file1, @Nullable File file2, @NotNull List<PathMapping> list, @NotNull List<String> list1) throws ExecutionException {
        throw new ExecutionException("Not support core dump2 ");
    }

    @Override
    public @NotNull Inferior loadForRemote(@NotNull String s, @Nullable File file, @Nullable File file1, @NotNull List<PathMapping> list) throws ExecutionException {
        throw new ExecutionException("Remote is not supported");
    }


    @Override
    public boolean interrupt() throws ExecutionException {
        handleInterrupted(new StopPlace(fakeThreads.get(0),
                new LLFrame(1, "main", null, null, 3, 12, StandardDebuggerLanguage.C, false, false, "super")
        ));
        //todo
        return true;
    }

    @Override
    public boolean resume() throws ExecutionException {
        handleRunning();
        handleTargetOutput("Say Something?\n", ProcessOutputTypes.STDOUT);
        handleTargetOutput("Something!\n", ProcessOutputTypes.STDERR);
        return true;
    }


    @Override
    public void stepOver(@Nullable Boolean aBoolean) throws ExecutionException {
        handleRunning();
        fakeSrcFileLine = (fakeSrcFileLine + 1) % 6;
        handleInterrupted(new StopPlace(fakeThreads.get(0),
                new LLFrame(1, "main", fakeSrcFileName, null, fakeSrcFileLine, 14, StandardDebuggerLanguage.C, false, false, "super"))
        );
    }

    @Override
    public void stepInto(boolean b, @Nullable Boolean aBoolean) throws ExecutionException {
        stepOver(aBoolean);
    }

    @Override
    public void stepOut() throws ExecutionException {
        stepOver(false);
    }

    @Override
    public void runTo(@NotNull String s, int i) throws ExecutionException {
        handleRunning();
    }

    @Override
    public void runTo(@NotNull Address address) throws ExecutionException {
        handleRunning();
    }

    @Override
    public @NotNull StopPlace jumpToLine(@NotNull LLThread llThread, @NotNull String s, int i, boolean b) throws ExecutionException, DebuggerCommandException {
        return new StopPlace(fakeThreads.get(0),
                new LLFrame(1, "mainjump2", fakeSrcFileName, null, fakeSrcFileLine, 14, StandardDebuggerLanguage.C, false, false, "super"));
    }

    @Override
    public @NotNull StopPlace jumpToAddress(@NotNull LLThread llThread, @NotNull Address address, boolean b) throws ExecutionException, DebuggerCommandException {
        return new StopPlace(fakeThreads.get(0),
                new LLFrame(1, "mainjump21", fakeSrcFileName, null, fakeSrcFileLine, 14, StandardDebuggerLanguage.C, false, false, "super"));
    }

    @Override
    public void addPathMapping(int i, @NotNull String s, @NotNull String s1) throws ExecutionException {

    }

    @Override
    public void addForcedFileMapping(int i, @NotNull String s, @Nullable DebuggerSourceFileHash debuggerSourceFileHash, @NotNull String s1) throws ExecutionException {

    }

    @Override
    protected boolean doExit() throws ExecutionException {
        handleExited(0);
        return true;
    }

    @Override
    public @NotNull LLWatchpoint addWatchpoint(long l, int i, @NotNull LLValue llValue, @NotNull String s, LLWatchpoint.@Nullable Lifetime lifetime, LLWatchpoint.@NotNull AccessType accessType) throws ExecutionException, DebuggerCommandException {
        return new LLWatchpoint(lastId.incrementAndGet(), s);
    }

    @Override
    public void removeWatchpoint(@NotNull List<Integer> list) throws ExecutionException, DebuggerCommandException {

    }

    @Override
    public @NotNull LLBreakpoint addBreakpoint(@NotNull String s, int i, @Nullable String s1, boolean b) throws ExecutionException, DebuggerCommandException {
        ArrayList<LLBreakpointLocation> llBreakpointLocations = new ArrayList<>();
        llBreakpointLocations.add(new LLBreakpointLocation("create", "32131", 2, Address.NULL));
        return new LLBreakpoint(lastId.incrementAndGet(), s, i, llBreakpointLocations, "asdasd");
    }

    @Override
    public @NotNull LLBreakpoint addAddressBreakpoint(@NotNull Address address, @Nullable String s) throws ExecutionException, DebuggerCommandException {
        ArrayList<LLBreakpointLocation> llBreakpointLocations = new ArrayList<>();
        llBreakpointLocations.add(new LLBreakpointLocation("create", "32131", 2,address));
        return new LLBreakpoint(lastId.incrementAndGet(), s, 123, llBreakpointLocations, "asdasd");
    }

    @Override
    public @Nullable LLSymbolicBreakpoint addSymbolicBreakpoint(@NotNull SymbolicBreakpoint symbolicBreakpoint) throws ExecutionException, DebuggerCommandException {
        return new LLSymbolicBreakpoint(lastId.incrementAndGet());
    }

    @Override
    public void removeCodepoints(@NotNull Collection<Integer> collection) throws ExecutionException, DebuggerCommandException {

    }

    @Override
    public @NotNull List<LLThread> getThreads() throws ExecutionException, DebuggerCommandException {
        return fakeThreads;
    }

    @Override
    public @NotNull ResultList<LLFrame> getFrames(long l, int i, int i1, boolean b) throws ExecutionException, DebuggerCommandException {
        return new ResultList<>(Collections.singletonList(
                new LLFrame(1, "main", fakeSrcFileName, null, fakeSrcFileLine, 14, StandardDebuggerLanguage.C, false, false, "super")
        ), false);
    }

    @Override
    public @NotNull List<LLValue> getVariables(long l, int i) throws ExecutionException, DebuggerCommandException {
        return Collections.singletonList(new LLValue("foo", "char*", 123L, null, "foo"));
    }

    @Override
    public @NotNull LLValueData getData(@NotNull LLValue llValue) throws ExecutionException, DebuggerCommandException {
        return new LLValueData("foo".equals(llValue.getName()) ? "\"bar\"" : "<->", null, false, false, false);
    }

    @Override
    public @Nullable String getDescription(@NotNull LLValue llValue, int i) throws ExecutionException, DebuggerCommandException {
        return null;
    }

    @Override
    public @Nullable Integer getChildrenCount(@NotNull LLValue llValue) throws ExecutionException, DebuggerCommandException {
        return null;
    }

    @Override
    public @NotNull ResultList<LLValue> getVariableChildren(@NotNull LLValue llValue, int i, int i1) throws ExecutionException, DebuggerCommandException {
        throw new DebuggerCommandException("Not implemented");
    }

    @Override
    public @NotNull LLValue evaluate(long l, int i, @NotNull String s, @Nullable DebuggerLanguage debuggerLanguage) throws ExecutionException, DebuggerCommandException {
        if (s.equals(new LLValue("foo", "char*", 100L, null, "foo").getName())) {

            return new LLValue("foo", "char*",100L, null, "foo");
        } else {
            return new LLValue("<unknown>", "<unknown>",100L, null, "<unknown>");
        }
    }

    @Override
    public @NotNull List<LLInstruction> disassembleFunction(@NotNull Address address, @NotNull AddressRange addressRange) throws ExecutionException, DebuggerCommandException {
        return Arrays.asList(
                        LLInstruction.create(address, "55", "FAKE.NOP", "No-op", null),
                        LLInstruction.create(address.plus(1), "AA", "FAKE.RET", "Return", null)
                );
    }

    @Override
    public @NotNull List<LLInstruction> disassemble(@NotNull AddressRange addressRange) throws ExecutionException, DebuggerCommandException {
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<LLMemoryHunk> dumpMemory(@NotNull AddressRange addressRange) throws ExecutionException, DebuggerCommandException {
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<LLModule> getLoadedModules() throws ExecutionException, DebuggerCommandException {
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<LLSection> getModuleSections(@NotNull LLModule llModule) throws ExecutionException, DebuggerCommandException {
        return Collections.emptyList();
    }

    @Override
    public @NotNull ShellCommandResult executeShellCommand(@NotNull String s, @Nullable List<String> list, @Nullable String s1, int i) throws ExecutionException {
        throw new ExecutionException("Shell is not Supported");
    }

    @Override
    public void executeConsoleCommand(@NotNull String s) throws ExecutionException, DebuggerCommandException {

    }

    @Override
    public void executeConsoleCommand(long l, int i, @NotNull String s) throws ExecutionException, DebuggerCommandException {

    }


    @Override
    public @NotNull String executeInterpreterCommand(@NotNull String s) throws ExecutionException, DebuggerCommandException {
        return null;
    }

    @Override
    public @NotNull String executeInterpreterCommand(long l, int i, @NotNull String s) throws ExecutionException, DebuggerCommandException {
        return null;
    }

    @Override
    public @NotNull ResultList<String> completeConsoleCommand(@NotNull String s, int i) throws ExecutionException {
        return new ResultList<>(Collections.emptyList(), false);
    }

    @Override
    public void handleSignal(@NotNull String s, boolean b, boolean b1, boolean b2) throws ExecutionException, DebuggerCommandException {

    }

    @Override
    public void cancelSymbolsDownload(@NotNull String s) throws ExecutionException, DebuggerCommandException {

    }

    @Override
    protected String getPromptText() {
        return "bug-eater";
    }

    @Override
    public void checkErrors() throws ExecutionException {

    }

    @Override
    public void addSymbolsFile(@NotNull File file, @Nullable File file1) throws ExecutionException {

    }


    private class FakeInferior extends Inferior {

        private FakeInferior(@Nullable Project project) {
            super(BugEaterDriver2.this.lastId.incrementAndGet());
            if (project!=null && project.getBasePath() != null) {
                fakeSrcFileName = Paths.get(project.getBasePath(), "main.cpp").toString();
            }
        }

        @Override
        protected long startImpl() throws ExecutionException {
            //todo These methods are supposed to be called asynchronously when actual debugger is started
            handleAttached(0);
            handleRunning();
            return 0;
        }

        @Override
        protected void detachImpl() throws ExecutionException {
            //todo These methods are supposed to be called asynchronously when actual debugger is stopped
            handleDetached();
        }

        @Override
        protected boolean destroyImpl() throws ExecutionException {
            //todo These methods are supposed to be called asynchronously when actual debugger is disconnected
            handleDisconnected();
            return true;
        }
    }
}
