import express from 'express'
const router = express.Router();


/* GET users listing. */
router.get('/u', function(req, res, next) {
  res.send('respond with a resource');
});

export default router;