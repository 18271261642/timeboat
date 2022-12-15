package com.imlaidian.utilslibrary.utils;


public  final class Preconditions {

    public static <T> T checkNotNull(T reference) {
        try {
            if (reference == null) {
                throw new NullPointerException();
            }
        }catch ( Exception e){
            e.printStackTrace();
            LogUtil.s("Preconditions" , "checkNotNull  exception :" , e);
        }

        return reference;
    }

    public static void checkAllNotNull(Object... references) {

            for (Object reference : references) {
                try {
                    if (reference == null) {
                        throw new NullPointerException();
                    }
                }catch (Exception e){
                    LogUtil.s("Preconditions" , "checkAllNotNull  exception :" , e);
                }

            }
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        try{
            if (reference == null) {
                throw new NullPointerException(errorMessage);
            }
        }catch (Exception e){
            LogUtil.s("Preconditions" , "checkNotNull  errorMessage :" +errorMessage  + ",exception :", e);
        }

        return reference;
    }

    public static void checkArgument(boolean expression) {
        try{
            if (!expression) {
                throw new IllegalArgumentException();
            }
        }catch (Exception e){
            LogUtil.s("Preconditions" , "checkArgument exception", e);
        }


    }
    public static void checkArgument(boolean expression, String errorMessage) {
        try{
            if (!expression) {
                throw new IllegalArgumentException(errorMessage);
            }
        }catch (Exception e){
            LogUtil.s("Preconditions" , "checkArgument errorMessage=" +errorMessage + ",exception=", e);
        }


    }
}
