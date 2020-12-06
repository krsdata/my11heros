package com.my11heros.bottom_nav;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.my11heros.R;


public class BottomNavBar extends CoordinatorLayout {
    int fabMenuIndex = 0;
    private static final String TAG = BottomNavBar.class.getSimpleName();
    public BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;


    public BottomNavBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        inflate(getContext(), R.layout.bottom_nav_bar, this);
        inflate(getContext(), R.layout.bottom_nav_bar, this);

        // initialize the bottom nav and fab views
        this.bottomNavigationView = findViewById(R.id.nav_view);
        this.fab = findViewById(R.id.fab);
        this.fab1 = findViewById(R.id.fab1);
        this.fab2 = findViewById(R.id.fab2);
        this.fab3 = findViewById(R.id.fab3);

        TypedArray styleAttrs = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BottomNavBar, 0, 0);

        Float curveMargin = styleAttrs.getDimension(R.styleable.BottomNavBar_bn_curve_margin, 0F);
        Float roundedCornerRadius = styleAttrs.getDimension(R.styleable.BottomNavBar_bn_curve_rounded_corner_radius, 0F);
        Float cradleVerticalOffset = styleAttrs.getDimension(R.styleable.BottomNavBar_bn_curve_vertical_offset, 0F);
        @IdRes int menuResID = styleAttrs.getResourceId(R.styleable.BottomNavBar_bn_menu, -1);
//         fabMenuIndex = styleAttrs.getInt(R.styleable.BottomNavBar_bn_fab_menu_index, -1);
        ColorStateList menuItemColorState = styleAttrs.getColorStateList(R.styleable.BottomNavBar_bn_item_color);
        ColorStateList fabBackgroundColor = styleAttrs.getColorStateList(R.styleable.BottomNavBar_bn_fab_background_color);
        ColorStateList fabIconColor = styleAttrs.getColorStateList(R.styleable.BottomNavBar_bn_fab_icon_color);

        Float fabDiameter;
        int fabSize;
        String bnFabSize = styleAttrs.getString(R.styleable.BottomNavBar_bn_fab_size);
        if (bnFabSize == null || bnFabSize.equalsIgnoreCase("normal")) {
            // set fab param to default fab parameters
            fabDiameter = getResources().getDimension(R.dimen._56sdp);
            fabSize = FloatingActionButton.SIZE_NORMAL;
        } else if (bnFabSize.equalsIgnoreCase("mini")) {
            fabDiameter = getResources().getDimension(R.dimen._40sdp);
            fabSize = FloatingActionButton.SIZE_MINI;
        } else {
            Log.e(TAG, "bnFabSize should be either 'mini' or 'normal'");
            throw new IllegalArgumentException("bnFabSize '" + bnFabSize + "' does not match any of the fab size values");
        }

        // check if no menu has been added to BottomNav view
        if (menuResID == -1) {
            Log.e(TAG, "menuResID should not be null.");
            throw new Resources.NotFoundException("Menu must be added to BottomNav.");
        }

        // add the menu to the bottom-nav
        bottomNavigationView.inflateMenu(menuResID);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setItemIconTintList(menuItemColorState);
        bottomNavigationView.setItemTextColor(menuItemColorState);
        // set listener on the bottomNavigationView

        // set listener on the fab
       /* fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(fabMenuIndex).getItemId());
            }
        });*/

        // check if fab is not added
        if (fabMenuIndex == -1) {
            fab.setVisibility(GONE);
            Log.w(TAG, "No fab is added");
            return;
        }

        // check if fab position does not match any menu item position
        if (fabMenuIndex >= bottomNavigationView.getMenu().size()) {
            fab.setVisibility(GONE);
            Log.e(TAG, "fabMenuIndex is out of bound. fabMenuIndex only accepts values from 0 to " + (bottomNavigationView.getMenu().size() - 1));
            throw new IllegalArgumentException("fabMenuIndex does not match any menu item position");
        }

        // show fab
        fab.setVisibility(VISIBLE);

        // set fav size
        fab.setSize(fabSize);

        // set the fab background tint
        fab.setBackgroundTintList(fabBackgroundColor);

        // set the fab icon tint
        fab.setImageTintList(fabIconColor);

        TopEdgeTreatment topEdgeTreatment = new TopEdgeTreatment(bottomNavigationView.getMenu().size(), fabMenuIndex, curveMargin, roundedCornerRadius, cradleVerticalOffset);
        topEdgeTreatment.setFabDiameter(fabDiameter);

       /* MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        ShapeAppearanceModel shapeAppearanceModel = materialShapeDrawable.getShapeAppearanceModel()
                .toBuilder().setTopEdge(topEdgeTreatment).build();

        materialShapeDrawable.setTint(Color.TRANSPARENT);
        materialShapeDrawable.setShapeAppearanceModel(shapeAppearanceModel);

        ViewCompat.setBackground(bottomNavigationView, materialShapeDrawable);*/

        // get the icon from menu item and set it to the fab
        fab.setImageDrawable(bottomNavigationView.getMenu().getItem(fabMenuIndex).getIcon());
        bottomNavigationView.getMenu().getItem(fabMenuIndex).setIcon(null);
        fab1.setSize(fabSize);

        // set the fab background tint
        fab1.setBackgroundTintList(fabBackgroundColor);

        // set the fab icon tint
        fab1.setImageTintList(fabIconColor);

        TopEdgeTreatment topEdgeTreatment1 = new TopEdgeTreatment(bottomNavigationView.getMenu().size(), 1, curveMargin, roundedCornerRadius, cradleVerticalOffset);
        topEdgeTreatment1.setFabDiameter(fabDiameter);

       /* MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        ShapeAppearanceModel shapeAppearanceModel = materialShapeDrawable.getShapeAppearanceModel()
                .toBuilder().setTopEdge(topEdgeTreatment).build();

        materialShapeDrawable.setTint(Color.TRANSPARENT);
        materialShapeDrawable.setShapeAppearanceModel(shapeAppearanceModel);

        ViewCompat.setBackground(bottomNavigationView, materialShapeDrawable);*/

        // get the icon from menu item and set it to the fab
        fab1.setImageDrawable(bottomNavigationView.getMenu().getItem(1).getIcon());


        fab2.setSize(fabSize);

        // set the fab background tint
        fab2.setBackgroundTintList(fabBackgroundColor);

        // set the fab icon tint
        fab2.setImageTintList(fabIconColor);

        TopEdgeTreatment topEdgeTreatment2 = new TopEdgeTreatment(bottomNavigationView.getMenu().size(), 2, curveMargin, roundedCornerRadius, cradleVerticalOffset);
        topEdgeTreatment2.setFabDiameter(fabDiameter);

       /* MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        ShapeAppearanceModel shapeAppearanceModel = materialShapeDrawable.getShapeAppearanceModel()
                .toBuilder().setTopEdge(topEdgeTreatment).build();

        materialShapeDrawable.setTint(Color.TRANSPARENT);
        materialShapeDrawable.setShapeAppearanceModel(shapeAppearanceModel);

        ViewCompat.setBackground(bottomNavigationView, materialShapeDrawable);*/

        // get the icon from menu item and set it to the fab
        fab2.setImageDrawable(bottomNavigationView.getMenu().getItem(2).getIcon());

        fab3.setSize(fabSize);

        // set the fab background tint
        fab3.setBackgroundTintList(fabBackgroundColor);

        // set the fab icon tint
        fab3.setImageTintList(fabIconColor);

        TopEdgeTreatment topEdgeTreatment3 = new TopEdgeTreatment(bottomNavigationView.getMenu().size(), 3, curveMargin, roundedCornerRadius, cradleVerticalOffset);
        topEdgeTreatment3.setFabDiameter(fabDiameter);

       /* MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        ShapeAppearanceModel shapeAppearanceModel = materialShapeDrawable.getShapeAppearanceModel()
                .toBuilder().setTopEdge(topEdgeTreatment).build();

        materialShapeDrawable.setTint(Color.TRANSPARENT);
        materialShapeDrawable.setShapeAppearanceModel(shapeAppearanceModel);

        ViewCompat.setBackground(bottomNavigationView, materialShapeDrawable);*/

        // get the icon from menu item and set it to the fab
        fab3.setImageDrawable(bottomNavigationView.getMenu().getItem(3).getIcon());

        // remove the icon from the item in the bottom-nav
//        bottomNavigationView.getMenu().getItem(fabMenuIndex).setIcon(null);
        fab1.setVisibility(GONE);
        fab2.setVisibility(GONE);
        fab3.setVisibility(GONE);
        // get an instance of the 1st menu item, as we need it to
        final BottomNavigationItemView bottomNavigationItemView = (BottomNavigationItemView) ((BottomNavigationMenuView) bottomNavigationView.getChildAt(0)).getChildAt(1);

        bottomNavigationView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // kill the global layout listener so it does not keep calling the layout
                bottomNavigationItemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // calculate the distance between the fab center and the bottom_nav's left edge
                int fabMarginLeft = (fabMenuIndex * bottomNavigationItemView.getMeasuredWidth()) + (bottomNavigationItemView.getMeasuredWidth() / 2) - (fab.getMeasuredWidth() / 2);

                //get the fab layout params
                MarginLayoutParams mp = (MarginLayoutParams) fab.getLayoutParams();

                // set the fab margins
                mp.setMargins(fabMarginLeft, 0, 0, 0);

                // apply changes and initial the fab view
                fab.requestLayout();
                int fabMarginLeft1 = (1 * bottomNavigationItemView.getMeasuredWidth()) + (bottomNavigationItemView.getMeasuredWidth() / 2) - (fab.getMeasuredWidth() / 2);
                //get the fab layout params
                MarginLayoutParams mp1 = (MarginLayoutParams) fab1.getLayoutParams();
                // set the fab margins
                mp1.setMargins(fabMarginLeft1, 0, 0, 0);
                fab1.requestLayout();
                int fabMarginLeft2 = (2 * bottomNavigationItemView.getMeasuredWidth()) + (bottomNavigationItemView.getMeasuredWidth() / 2) - (fab.getMeasuredWidth() / 2);
                //get the fab layout params
                MarginLayoutParams mp2 = (MarginLayoutParams) fab2.getLayoutParams();
                // set the fab margins
                mp2.setMargins(fabMarginLeft2, 0, 0, 0);
                fab2.requestLayout();
                int fabMarginLeft3 = (3 * bottomNavigationItemView.getMeasuredWidth()) + (bottomNavigationItemView.getMeasuredWidth() / 2) - (fab.getMeasuredWidth() / 2);
                //get the fab layout params
                MarginLayoutParams mp3 = (MarginLayoutParams) fab3.getLayoutParams();
                // set the fab margins
                mp3.setMargins(fabMarginLeft3, 0, 0, 0);
                fab3.requestLayout();
            }
        });
    }


    public void updateFabPosition(int position) {
        if (fabMenuIndex != position) {
            fab.hide();
            fabMenuIndex = position;
            // show fab
            // get the icon from menu item and set it to the fab
            fab.setImageDrawable(bottomNavigationView.getMenu().getItem(fabMenuIndex).getIcon());
            // get an instance of the 1st menu item, as we need it to
            final BottomNavigationItemView bottomNavigationItemView = (BottomNavigationItemView) ((BottomNavigationMenuView) bottomNavigationView.getChildAt(0)).getChildAt(1);
            int fabMarginLeft = (fabMenuIndex * bottomNavigationItemView.getMeasuredWidth()) + (bottomNavigationItemView.getMeasuredWidth() / 2) - (fab.getMeasuredWidth() / 2);
            //get the fab layout params
            MarginLayoutParams mp = (MarginLayoutParams) fab.getLayoutParams();
            // set the fab margins
            mp.setMargins(fabMarginLeft, 0, 0, 0);
            fab.show();
        }
    }

    public void updateFabPosition2(int position) {
        if (fabMenuIndex != position) {
            switch (fabMenuIndex) {
                case 0:
                    fab.setVisibility(GONE);
                    bottomNavigationView.getMenu().getItem(fabMenuIndex).setIcon(fab.getDrawable());
                    break;
                case 1:
                    fab1.setVisibility(GONE);
                    bottomNavigationView.getMenu().getItem(fabMenuIndex).setIcon(fab1.getDrawable());
                    break;
                case 2:
                    fab2.setVisibility(GONE);
                    bottomNavigationView.getMenu().getItem(fabMenuIndex).setIcon(fab2.getDrawable());
                    break;
                case 3:
                    fab3.setVisibility(GONE);
                    bottomNavigationView.getMenu().getItem(fabMenuIndex).setIcon(fab3.getDrawable());
                    break;
            }
            switch (position) {
                case 0:
                    fab.setVisibility(VISIBLE);
                    bottomNavigationView.getMenu().getItem(position).setIcon(null);
                    break;
                case 1:
                    fab1.setVisibility(VISIBLE);
                    bottomNavigationView.getMenu().getItem(position).setIcon(null);
                    break;
                case 2:
                    fab2.setVisibility(VISIBLE);
                    bottomNavigationView.getMenu().getItem(position).setIcon(null);
                    break;
                case 3:
                    fab3.setVisibility(VISIBLE);
                    bottomNavigationView.getMenu().getItem(position).setIcon(null);
                    break;
            }
            fabMenuIndex = position;
        }
    }
}
