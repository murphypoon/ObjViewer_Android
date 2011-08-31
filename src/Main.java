package nz.gen.geek_central.ObjViewer;

import nz.gen.geek_central.GLUseful.ObjReader;

public class Main extends android.app.Activity
  {
    java.util.Map<android.view.MenuItem, Runnable> OptionsMenu;

    ObjectView TheObjectView;

  /* request codes, all arbitrarily assigned */
    static final int LoadObjectRequest = 1;

    interface RequestResponseAction /* response to an activity result */
      {
        public void Run
          (
            int ResultCode,
            android.content.Intent Data
          );
      } /*RequestResponseAction*/

    java.util.Map<Integer, RequestResponseAction> ActivityResultActions;

    private interface SelectedIDAction
      {
        public void Set
          (
            int SelectedID
          ); 
      } /*SelectedIDAction*/

    private class OptionsDialog
        extends android.app.Dialog
        implements android.content.DialogInterface.OnDismissListener
      {
        private final android.content.Context ctx;
        private final String Title;
        private final SelectedIDAction Action;
        private final int InitialButtonID;
        private class ButtonDef
          {
            final String ButtonTitle;
            final int ButtonID;

            public ButtonDef
              (
                String ButtonTitle,
                int ButtonID
              )
              {
                this.ButtonTitle = ButtonTitle;
                this.ButtonID = ButtonID;
              } /*ButtonDef*/
          } /*ButtonDef*/
        private final java.util.ArrayList<ButtonDef> TheButtonDefs =
            new java.util.ArrayList<ButtonDef>();
        private android.widget.RadioGroup TheButtons;

        public OptionsDialog
          (
            android.content.Context ctx,
            String Title,
            SelectedIDAction Action,
            int InitialButtonID
          )
          {
            super(ctx);
            this.ctx = ctx;
            this.Title = Title;
            this.Action = Action;
            this.InitialButtonID = InitialButtonID;
          } /*OptionsDialog*/

        public OptionsDialog AddButton
          (
            String ButtonTitle,
            int ButtonID
          )
          {
            TheButtonDefs.add(new ButtonDef(ButtonTitle, ButtonID));
            return
                this;
          } /*AddButton*/

        @Override
        public void onCreate
          (
            android.os.Bundle savedInstanceState
          )
          {
            setTitle(Title);
            final android.widget.LinearLayout MainLayout = new android.widget.LinearLayout(ctx);
            MainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
            setContentView(MainLayout);
            TheButtons = new android.widget.RadioGroup(ctx);
            final android.view.ViewGroup.LayoutParams ButtonLayout =
                new android.view.ViewGroup.LayoutParams
                  (
                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                  );
            for (ButtonDef ThisButtonDef : TheButtonDefs)
              {
                final android.widget.RadioButton ThisButton =
                    new android.widget.RadioButton(ctx);
                ThisButton.setText(ThisButtonDef.ButtonTitle);
                ThisButton.setId(ThisButtonDef.ButtonID);
                TheButtons.addView(ThisButton, TheButtons.getChildCount(), ButtonLayout);
              } /*for*/
            MainLayout.addView(TheButtons, ButtonLayout);
            TheButtons.check(InitialButtonID);
            setOnDismissListener(this);
          } /*onCreate*/

        @Override
        public void onDismiss
          (
            android.content.DialogInterface TheDialog
          )
          {
            Action.Set(TheButtons.getCheckedRadioButtonId());
          } /*onDismiss*/

      } /*OptionsDialog*/

    @Override
    public boolean onCreateOptionsMenu
      (
        android.view.Menu TheMenu
      )
      {
        OptionsMenu = new java.util.HashMap<android.view.MenuItem, Runnable>();
        OptionsMenu.put
          (
            TheMenu.add(R.string.pick_file),
            new Runnable()
              {
                public void run()
                  {
                    Picker.Launch
                      (
                        /*Acting =*/ Main.this,
                        /*RequestCode =*/ LoadObjectRequest,
                        /*LookIn =*/
                            new String[]
                                {
                                    "Models",
                                    "Download",
                                }
                      );
                  } /*run*/
              } /*Runnable*/
          );
        OptionsMenu.put
          (
            TheMenu.add(R.string.reset_view),
            new Runnable()
              {
                public void run()
                  {
                    TheObjectView.ResetOrientation();
                  } /*run*/
              } /*Runnable*/
          );
        OptionsMenu.put
          (
            TheMenu.add(R.string.options_lighting),
            new Runnable()
              {
                public void run()
                  {
                    new OptionsDialog
                      (
                        /*ctx =*/ Main.this,
                        /*Title =*/ getString(R.string.lighting_title),
                        /*Action =*/
                            new SelectedIDAction()
                              {
                                public void Set
                                  (
                                    int SelectedID
                                  )
                                  {
                                    TheObjectView.SetUseLighting(SelectedID != 0);
                                  } /*Set*/
                              } /*SelectedIDAction*/,
                        /*InitialButtonID =*/ TheObjectView.GetUseLighting() ? 1 : 0
                      )
                        .AddButton(getString(R.string.on), 1)
                        .AddButton(getString(R.string.off), 0)
                        .show();
                  } /*run*/
              } /*Runnable*/
          );
        OptionsMenu.put
          (
            TheMenu.add(R.string.options_orient_faces),
            new Runnable()
              {
                public void run()
                  {
                    new OptionsDialog
                      (
                        /*ctx =*/ Main.this,
                        /*Title =*/ getString(R.string.orient_faces_title),
                        /*Action =*/
                            new SelectedIDAction()
                              {
                                public void Set
                                  (
                                    int SelectedID
                                  )
                                  {
                                    TheObjectView.SetClockwiseFaces(SelectedID != 0);
                                  } /*Set*/
                              } /*SelectedIDAction*/,
                        /*InitialButtonID =*/ TheObjectView.GetClockwiseFaces() ? 1 : 0
                      )
                        .AddButton(getString(R.string.anticlockwise), 0)
                        .AddButton(getString(R.string.clockwise), 1)
                        .show();
                  } /*run*/
              } /*Runnable*/
          );
        return
            true;
      } /*onCreateOptionsMenu*/

    void BuildActivityResultActions()
      {
        ActivityResultActions = new java.util.HashMap<Integer, RequestResponseAction>();
        ActivityResultActions.put
          (
            LoadObjectRequest,
            new RequestResponseAction()
              {
                public void Run
                  (
                    int ResultCode,
                    android.content.Intent Data
                  )
                  {
                    final String ObjFileName = Data.getData().getPath();
                    ObjReader.Model NewObj = null;
                    try
                      {
                        NewObj = ObjReader.ReadObj
                          (
                            /*FileName =*/ ObjFileName,
                            /*LoadMaterials =*/
                                new ObjReader.MaterialLoader()
                                  {
                                    public ObjReader.MaterialSet Load
                                      (
                                        ObjReader.MaterialSet Materials,
                                        String MatFileName
                                      )
                                      {
                                        return
                                            ObjReader.ReadMaterials
                                              (
                                                /*FileName =*/
                                                    new java.io.File
                                                      (
                                                        new java.io.File(ObjFileName)
                                                            .getParentFile(),
                                                        MatFileName
                                                      ).getPath(),
                                                /*CurMaterials =*/ Materials
                                              );
                                      } /*Load*/
                                  } /*MaterialLoader*/
                          );
                      }
                    catch (ObjReader.DataFormatException Failed)
                      {
                        android.widget.Toast.makeText
                          (
                            /*context =*/ Main.this,
                            /*text =*/
                                String.format
                                  (
                                    getString(R.string.obj_load_fail),
                                    Failed.toString()
                                  ),
                            /*duration =*/ android.widget.Toast.LENGTH_SHORT
                          ).show();
                      } /*try*/
                    if (NewObj != null)
                      {
                        TheObjectView.SetObject(NewObj);
                      } /*if*/
                  } /*Run*/
              } /*RequestResponseAction*/
          );
      } /*BuildActivityResultActions*/

    @Override
    public void onCreate
      (
        android.os.Bundle SavedInstanceState
      )
      {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.main);
        TheObjectView = (ObjectView)findViewById(R.id.object_view);
        BuildActivityResultActions();
      } /*onCreate*/

    @Override
    public boolean onOptionsItemSelected
      (
        android.view.MenuItem TheItem
      )
      {
        boolean Handled = false;
        final Runnable Action = OptionsMenu.get(TheItem);
        if (Action != null)
          {
            Action.run();
            Handled = true;
          } /*if*/
        return
            Handled;
      } /*onOptionsItemSelected*/

    @Override
    public void onActivityResult
      (
        int RequestCode,
        int ResultCode,
        android.content.Intent Data
      )
      {
        Picker.Cleanup();
        if (ResultCode != android.app.Activity.RESULT_CANCELED)
          {
            final RequestResponseAction Action = ActivityResultActions.get(RequestCode);
            if (Action != null)
              {
                Action.Run(ResultCode, Data);
              } /*if*/
          } /*if*/
      } /*onActivityResult*/

  } /*Main*/