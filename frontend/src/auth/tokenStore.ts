let accessToken: string | null = null;

export const tokenStore = {
  getAccessToken: () => accessToken,
  setAccessToken: (token: string | null) => {
    accessToken = token;
    console.log("[tokenStore] setAccessToken ->", token);
  },
};
