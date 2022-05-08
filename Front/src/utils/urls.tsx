export const restApiUrl = `http://apppalstaticserver-env.eba-rixzt96v.us-east-2.elasticbeanstalk.com/`;

const API_USER = `${restApiUrl}user`;

export const apiUrls = {
  index: restApiUrl,
  user: `${API_USER}`,
  login: `${API_USER}/login`,
}